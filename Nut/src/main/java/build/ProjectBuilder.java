package nut.build;

import nut.artifact.Artifact;
import nut.build.BuildException;
import nut.model.Layout;
import nut.model.Project;
import nut.model.XmlReader;
import nut.model.ValidationException;
import nut.xml.XmlParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

/*
Notes
 * when the project is read it may not have a groupId, as it must be inherited
 * the inheritance assembler must use projects that are unadulterated!
*/

public class ProjectBuilder
{
    private String packagingPath;
    private String nutVersion;

    public ProjectBuilder()
    {
        // path of packaging projects
        this.packagingPath = System.getProperty( "nut.home", "." ) + File.separatorChar + "nut" + File.separatorChar + "packaging";
        this.nutVersion = System.getProperty( "nut.version", "1.0" );
    }

    // ----------------------------------------------------------------------
    // Build Implementation
    // ----------------------------------------------------------------------

    public Project launch( File projectFile )
        throws BuildException
    {
        String basedir     = projectFile.getAbsoluteFile().getParent();
        // First read the project's nut.xml
        Project project = readProject( projectFile );
        // add basedir property
        project.addProperty( "basedir", projectFile.getAbsoluteFile().getParent() );
        // add properties as "nut..."
        project.addProperties();

        // then read the packaging project if any
        Project packagingProject = null;
        File packagingFile = new File( packagingPath, project.getPackaging() + "-" + nutVersion + ".xml" );
        if( packagingFile.exists() ) {
            packagingProject = readProject( packagingFile );
        } else {
            throw new BuildException( "Packaging file not found '" + packagingFile + "'" );
        }
        // and at last the parent file if any
        Project parentProject = null;
        if( project.getParent() != null ) {
          File parentFile = new File( basedir, project.getParent() );
          if( parentFile.exists() ) {
            parentProject = readProject( parentFile );
          }
        }

        //log.info( "Building " + project.getId() );
        if( packagingProject != null ) {
          project.setLayout( mergedLayout( project.getLayout(), packagingProject.getLayout() ) );
          project.getDependencies().addAll( packagingProject.getDependencies() );
          project.getRepositories().addAll( packagingProject.getRepositories() );
          project.setBuild( packagingProject.getBuild() );
        }
        if( parentProject != null ) {
          project.setLayout( mergedLayout( project.getLayout(), parentProject.getLayout() ) );
          project.getDependencies().addAll( parentProject.getDependencies() );
          project.getRepositories().addAll( parentProject.getRepositories() );
          if( project.getGroupId() == null )
            project.setGroupId( parentProject.getGroupId() );
          if( project.getVersion() == null )
            project.setVersion( parentProject.getVersion() );
          if( project.getBuild() == null )
            project.setBuild( parentProject.getBuild() );
        }
        try {
          // Must validate before artifact construction to make sure dependencies are good
          project.validate( );
        } catch ( ValidationException e ) {
          throw new BuildException( project.getId() + ": " + e.getMessage(), e );
        }
        project.setArtifact( new Artifact( project.getGroupId(), project.getArtifactId(), project.getVersion(), project.getPackaging() ) );
        return project;
    }

    // ----------------------------------------------------------------------
    private Layout mergedLayout( Layout child, Layout parent )
    {
        if ( child == null )
            return( parent );
        if ( parent != null )
            child.merge( parent );
        return( child );
    }

    // ----------------------------------------------------------------------
    private Project readProject( File file ) throws BuildException
    {
        Project project = null;
        try
        {
            InputStream is        = new FileInputStream(file);
            Reader reader         = new InputStreamReader( is );
            XmlReader projectReader = new XmlReader();
            StringReader sReader  = projectStringReader( reader );
            project = projectReader.parseProject( sReader );
            reader.close();
        }
        catch ( XmlParserException e ) {
            throw new BuildException( "Parse error reading '" + file.getAbsolutePath() + "': "+ e.getMessage(), e );
        }
        catch ( FileNotFoundException e ) {
            throw new BuildException( "Could not find the project file '" + file.getAbsolutePath() + "'.", e );
        }
        catch ( IOException e ) {
            throw new BuildException( "Could not read the project file '" + file.getAbsolutePath() + "'.", e );
        }
        return project;
    }

    private StringReader projectStringReader( Reader reader ) throws IOException
    {
        StringWriter sw = new StringWriter();
        final char[] buffer = new char[1024 * 4];
        int n = 0;
        while ( -1 != ( n = reader.read( buffer ) ) )
        {
            sw.write( buffer, 0, n );
        }
        sw.flush();
        return new StringReader( sw.toString() );
    }

}
