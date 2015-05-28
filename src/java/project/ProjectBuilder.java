package nut.project;

import nut.artifact.Artifact;

import nut.logging.Log;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Model;
import nut.model.xmlReader;
import nut.model.ValidationException;

import nut.project.NutProject;
import nut.project.InvalidDependencyVersionException;

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
        this.packagingPath = System.getProperty( "nut.home", "." ) + File.separatorChar + "nut" + File.separatorChar + "packaging";
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
        Model model = readModel( "unknownModel", projectFile );
        // then read the packaging model if any
        Model packagingModel = null;
        File packagingFile = new File( packagingPath, model.getPackaging() + "-" + nutVersion + ".xml" );
        if( packagingFile.exists() )
        {
            packagingModel = readModel( "unknownModel", packagingFile );
        }
        else
        {
            log.warn( "No template for packaging '" + model.getPackaging() + "'" );
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

            project = new NutProject( model );
            Artifact projectArtifact = new Artifact( project.getGroupId(), project.getArtifactId(), project.getVersion(),
                                                     project.getPackaging(), null );
            project.setArtifact( projectArtifact );
            // Must validate before artifact construction to make sure dependencies are good
            model.validate( );
        }
        catch ( ValidationException e )
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
    private Model readModel( String projectId, File file )
        throws ProjectBuildingException
    {
        Model model = null;
        try
        {
            InputStream is        = new FileInputStream(file);
            Reader reader         = new InputStreamReader( is );
            xmlReader modelReader = new xmlReader();
            StringReader sReader  = modelStringReader( reader );
            model = modelReader.parseModel( sReader );
            reader.close();
        }
        catch ( XmlPullParserException e ) {
            throw new ProjectBuildingException( projectId,
                                                "Parse error reading nut.xml: " + e.getMessage(), e );
        }
        catch ( FileNotFoundException e ) {
            throw new ProjectBuildingException( projectId,
                                                "Could not find the model file '" + file.getAbsolutePath() + "'.", e );
        }
        catch ( IOException e ) {
            throw new ProjectBuildingException( projectId, "Failed to build model from file '" +
                file.getAbsolutePath() + "'.\nError: \'" + e.getLocalizedMessage() + "\'", e );
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
