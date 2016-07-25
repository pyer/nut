package nut.project;

import nut.artifact.Artifact;

import nut.model.Build;
import nut.model.Model;
import nut.model.XmlReader;
import nut.model.ValidationException;

import nut.project.Project;
import nut.project.BuildException;

import nut.xml.pull.XmlPullParserException;

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
 * when the model is read it may not have a groupId, as it must be inherited
 * the inheritance assembler must use models that are unadulterated!
*/

public class ProjectBuilder
{
    private String packagingPath;
    private String nutVersion;

    public ProjectBuilder()
    {
        // path of packaging models
        this.packagingPath = System.getProperty( "nut.home", "." ) + File.separatorChar + "nut" + File.separatorChar + "packaging";
        this.nutVersion = System.getProperty( "nut.version", "1.0" );
    }

    // ----------------------------------------------------------------------
    // Build Implementation
    // ----------------------------------------------------------------------

    public Project build( File projectFile )
        throws BuildException
    {
        String basedir     = projectFile.getAbsoluteFile().getParent();
        // First read the project's nut.xml
        Model model = readModel( projectFile );
        // add basedir property
        model.addProperty( "basedir", projectFile.getAbsoluteFile().getParent() );
        // add properties as "nut..."
        model.addProperties();

        // then read the packaging model if any
        Model packagingModel = null;
        File packagingFile = new File( packagingPath, model.getPackaging() + "-" + nutVersion + ".xml" );
        if( packagingFile.exists() ) {
            packagingModel = readModel( packagingFile );
        } else {
            throw new BuildException( "Packaging file not found '" + packagingFile + "'" );
        }
        // and at last the parent file if any
        Model parentModel = null;
        if( model.getParent() != null ) {
          File parentFile = new File( basedir, model.getParent() );
          if( parentFile.exists() ) {
            parentModel = readModel( parentFile );
          }
        }

        //log.info( "Building " + model.getId() );
        if( packagingModel != null ) {
          model.setBuild( mergedBuild( model.getBuild(), packagingModel.getBuild() ) );
          model.getDependencies().addAll( packagingModel.getDependencies() );
          model.getRepositories().addAll( packagingModel.getRepositories() );
        }
        if( parentModel != null ) {
          model.setBuild( mergedBuild( model.getBuild(), parentModel.getBuild() ) );
          model.getDependencies().addAll( parentModel.getDependencies() );
          model.getRepositories().addAll( parentModel.getRepositories() );
          if( model.getGroupId() == null )
            model.setGroupId( parentModel.getGroupId() );
          if( model.getVersion() == null )
            model.setVersion( parentModel.getVersion() );
        }
        Project project = new Project( model );
        Artifact projectArtifact = new Artifact( project.getGroupId(), project.getArtifactId(), project.getVersion(), project.getPackaging() );
        try {
          project.setArtifact( projectArtifact );
          // Must validate before artifact construction to make sure dependencies are good
          model.validate( );
        } catch ( ValidationException e ) {
          throw new BuildException( model.getId() + ": " + e.getMessage(), e );
        }
        return project;
    }

    // ----------------------------------------------------------------------
    private Build mergedBuild( Build childBuild, Build parentBuild )
    {
        if ( childBuild == null )
            return( parentBuild );
        if ( parentBuild != null )
        {
            if( childBuild.getSourceDirectory()==null )
                 childBuild.setSourceDirectory( parentBuild.getSourceDirectory() );
            if( childBuild.getResourceDirectory()==null )
                 childBuild.setResourceDirectory( parentBuild.getResourceDirectory() );
            if( childBuild.getTestSourceDirectory()==null )
                 childBuild.setTestSourceDirectory( parentBuild.getTestSourceDirectory() );
            if( childBuild.getTestResourceDirectory()==null )
                 childBuild.setTestResourceDirectory( parentBuild.getTestResourceDirectory() );
            if( childBuild.getTargetDirectory()==null )
                 childBuild.setTargetDirectory( parentBuild.getTargetDirectory() );
            if( childBuild.getOutputDirectory()==null )
                 childBuild.setOutputDirectory( parentBuild.getOutputDirectory() );
            if( childBuild.getTestOutputDirectory()==null )
                 childBuild.setTestOutputDirectory( parentBuild.getTestOutputDirectory() );
            if( childBuild.getTestReportDirectory()==null )
                 childBuild.setTestReportDirectory( parentBuild.getTestReportDirectory() );

            childBuild.getGoals().addAll( parentBuild.getGoals() );
        }
        return( childBuild );
    }

    // ----------------------------------------------------------------------
    private Model readModel( File file )
        throws BuildException
    {
        Model model = null;
        try
        {
            InputStream is        = new FileInputStream(file);
            Reader reader         = new InputStreamReader( is );
            XmlReader modelReader = new XmlReader();
            StringReader sReader  = modelStringReader( reader );
            model = modelReader.parseModel( sReader );
            reader.close();
        }
        catch ( XmlPullParserException e ) {
            throw new BuildException( "Parse error reading '" + file.getAbsolutePath() + "': "+ e.getMessage(), e );
        }
        catch ( FileNotFoundException e ) {
            throw new BuildException( "Could not find the model file '" + file.getAbsolutePath() + "'.", e );
        }
        catch ( IOException e ) {
            throw new BuildException( "Could not read the model file '" + file.getAbsolutePath() + "'.", e );
        }
        return model;
    }

    private StringReader modelStringReader( Reader reader )
        throws IOException
    {
        StringWriter sw = new StringWriter();
        final char[] buffer = new char[1024 * 4];
        int n = 0;
        while ( -1 != ( n = reader.read( buffer ) ) )
        {
            sw.write( buffer, 0, n );
        }
        sw.flush();
        String modelSource = sw.toString();
        return new StringReader( modelSource );
    }

}
