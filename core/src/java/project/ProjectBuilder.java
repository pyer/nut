package nut.project;

import nut.artifact.Artifact;
//import nut.artifact.ArtifactUtils;

import nut.logging.Log;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Model;
import nut.model.xmlReader;

import nut.project.InvalidDependencyVersionException;
import nut.project.NutProject;

import nut.project.validation.ModelValidationException;
import nut.project.validation.ModelValidator;

import nut.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/*
Notes
 * when the model is read it may not have a groupId, as it must be inherited
 * the inheritance assembler must use models that are unadulterated!
*/

public class ProjectBuilder
{
    private Log log;
    private String packagingPath;
    private String nutVersion;

    public ProjectBuilder( Log log )
    {
        this.log = log;
        // path of packaging models
        this.packagingPath = System.getProperty( "nut.home", "." ) + File.separatorChar + "nut" + File.separatorChar + "packaging" ;
        this.nutVersion = System.getProperty( "nut.version", "1.0" );
    }

    // ----------------------------------------------------------------------
    // NutProjectBuilder Implementation
    // ----------------------------------------------------------------------

    public NutProject build( File projectFile )
        throws ProjectBuildingException
    {
        String pomLocation = projectFile.getAbsolutePath();
        // First read the project's nut.xml
        Model model = readModel( "unknownModel", projectFile, true );
        // then read the packaging model if any
        Model packagingModel = null;
        File packagingFile = new File( packagingPath, model.getPackaging() + "-" + nutVersion + ".xml" );
        if( packagingFile.exists() )
        {
            packagingModel = readModel( "unknownModel", packagingFile, true );
        }
        // at last read the parent model if any
        Model parentModel = null;
        if( model.getParent() != null )
        {
            File parentFile = new File( projectFile.getParentFile() + "/" + model.getParent(), "nut.xml" );
            parentModel = readModel( "unknownModel", parentFile, true );
            if( model.getGroupId() == null )
                model.setGroupId( parentModel.getGroupId() );
            if( model.getVersion() == null )
                model.setVersion( parentModel.getVersion() );
        }
 
        //log.info( "Building " + model.getId() );
        model.addProperty( "basedir", projectFile.getAbsoluteFile().getParent() );
        // add properties as "nut..."
        for ( Enumeration en = System.getProperties().propertyNames(); en.hasMoreElements(); )
        {
            String key = (String) en.nextElement();
            if( key.startsWith( "nut." ) )
            {
                model.addProperty( key, System.getProperty(key) );
            }
        }
                
        NutProject project = null;
        try
        {
            if( packagingModel != null )
            {
                model.setBuild( mergedBuild( model.getBuild(), packagingModel.getBuild() ) );
                model.getDependencies().addAll( packagingModel.getDependencies() );
            }
            if( parentModel != null )
            {
                model.getDependencies().addAll( parentModel.getDependencies() );
            }

            project = new NutProject( model );
            Artifact projectArtifact = new Artifact( project.getGroupId(), project.getArtifactId(), project.getVersion(),
                                                     project.getPackaging(), null );
            project.setArtifact( projectArtifact );
            
            // Must validate before artifact construction to make sure dependencies are good
            ModelValidator validator = new ModelValidator( model );
            validator.validate( );
            
            Build build = model.getBuild();
            if( build != null )
            {
                model.addProperty( "build.directory", project.getBuild().getDirectory() );
                model.addProperty( "build.outputDirectory", project.getBuild().getOutputDirectory() );
                model.addProperty( "build.testOutputDirectory", project.getBuild().getTestOutputDirectory() );
                model.addProperty( "build.sourceDirectory", project.getBuild().getSourceDirectory() );
                model.addProperty( "build.testSourceDirectory", project.getBuild().getTestSourceDirectory() );
            }
            model.addProperty( "project.groupId", project.getArtifact().getGroupId() );
            model.addProperty( "project.artifactId", project.getArtifact().getArtifactId() );
            model.addProperty( "project.version", project.getArtifact().getVersion() );
            model.addProperty( "project.packaging", project.getPackaging() );
        }
        catch ( ModelValidationException e )
        {
            throw new InvalidProjectModelException( model.getId(), pomLocation, e.getMessage(), e );
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
            if( childBuild.getDirectory()==null )
                 childBuild.setDirectory( parentBuild.getDirectory() );
            if( childBuild.getOutputDirectory()==null )
                 childBuild.setOutputDirectory( parentBuild.getOutputDirectory() );
            if( childBuild.getTestOutputDirectory()==null )
                 childBuild.setTestOutputDirectory( parentBuild.getTestOutputDirectory() );
            if( childBuild.getSourceDirectory()==null )
                 childBuild.setSourceDirectory( parentBuild.getSourceDirectory() );
            if( childBuild.getTestSourceDirectory()==null )
                 childBuild.setTestSourceDirectory( parentBuild.getTestSourceDirectory() );

            childBuild.getPlugins().addAll( parentBuild.getPlugins() );
        }
        return( childBuild );
    }

    // ----------------------------------------------------------------------

    private Model readModel( String projectId,
                             File file,
                             boolean strict )
        throws ProjectBuildingException
    {
        Reader reader = null;
        Model model = null;
        //log.info( "readModel 1: " + projectId + " from file "+ file.getAbsolutePath() );
        try
        {
            InputStream is = new FileInputStream(file);
            reader = new InputStreamReader( is );
            model = readModel( projectId, file.getAbsolutePath(), reader, strict );
            reader.close();
        }
        catch ( FileNotFoundException e )
        {
            throw new ProjectBuildingException( projectId,
                                                "Could not find the model file '" + file.getAbsolutePath() + "'.", e );
        }
        catch ( IOException e )
        {
            throw new ProjectBuildingException( projectId, "Failed to build model from file '" +
                file.getAbsolutePath() + "'.\nError: \'" + e.getLocalizedMessage() + "\'", e );
        }
        return model;
    }

    private Model readModel( String projectId,
                             String pomLocation,
                             Reader reader,
                             boolean strict )
        throws IOException, InvalidProjectModelException
    {
        //String modelSource = IOUtil.toString( reader );

        StringWriter sw = new StringWriter();
        final char[] buffer = new char[1024 * 4];
        int n = 0;
        while ( -1 != ( n = reader.read( buffer ) ) )
        {
            sw.write( buffer, 0, n );
        }
        sw.flush();
        String modelSource = sw.toString();

        StringReader sReader = new StringReader( modelSource );
        //log.info( "readModel 2: " + projectId + " from  "+ pomLocation );

        try
        {
            xmlReader modelReader = new xmlReader();
            return modelReader.read( sReader, strict );
        }
        catch ( XmlPullParserException e )
        {
            throw new InvalidProjectModelException( projectId, pomLocation,
                                                    "Parse error reading nut.xml. Reason: " + e.getMessage(), e );
        }
    }

}
