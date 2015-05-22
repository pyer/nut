package nut.plugins;

import nut.logging.Log;
import nut.artifact.Artifact;
import nut.model.Dependency;
import nut.project.NutProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/*
This plugin processes files and dependencies to create a war directory
copy sourceDirectory files to outputDirectory
copy dependencies to outputLibDirectory

Default values in packaging war are:
    <directory>target</directory>
    <outputDirectory>target/war</outputDirectory>
    <testOutputDirectory>target/test</testOutputDirectory>
    <sourceDirectory>src/webapp</sourceDirectory>
    <testSourceDirectory>test/webapp</testSourceDirectory>
*/
public class depProcessor
{
    /** Instance logger */
    private static Log log;

    public static void execute( NutProject project, Log logger )
        throws Exception
    {
        log = logger;
        Properties pluginProperties = project.getModel().getProperties();
        String basedir              = (String)pluginProperties.getProperty( "basedir" );
        String repository           = (String)pluginProperties.getProperty( "nut.home" );
        String buildDirectory       = (String)pluginProperties.getProperty( "build.directory" );
        String warDirectory         = (String)pluginProperties.getProperty( "build.outputDirectory" );
        String libDirectory         = (String)pluginProperties.getProperty( "build.outputLibDirectory" );
        String groupId              = (String)pluginProperties.getProperty( "project.groupId" );
        String artifactId           = (String)pluginProperties.getProperty( "project.artifactId" );
        String version              = (String)pluginProperties.getProperty( "project.version" );
        String packaging            = (String)pluginProperties.getProperty( "project.packaging" );

        log.debug( "basedir                   = " + basedir );
        log.debug( "repository                = " + repository );
        log.debug( "build.directory           = " + buildDirectory );
        log.debug( "build.outputDirectory     = " + warDirectory );
        log.debug( "project.artifactId        = " + artifactId );
        log.debug( "project.version           = " + version );
        log.debug( "project.packaging         = " + packaging );

        log.info( "   Processing \'" + artifactId + "\'" );



        // Copy dependencies files
        List<String> dependencies = new ArrayList<String>();
        List modelDep = project.getModel().getDependencies();
        for ( int i = 0; i < modelDep.size(); i++ )
        {
            Dependency dep = (Dependency)(modelDep.get(i));
            Artifact artifactDep = new Artifact( dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getType(), null );
            File file = new File ( repository + File.separator + artifactDep.getPath() );
            String dest = basedir  + File.separator + warDirectory + File.separator + "WEB-INF" + File.separator + "lib" + File.separator + file.getName();
            copyFile(file.getAbsolutePath(), dest);
        }


/*
                String group = groupId.replace( '.', File.separatorChar );
                String artifactName = repository + File.separator + group + File.separator + artifactId + "-" + version + "." + packaging;
                //install: copy target file to local repository
                String buildName = basedir + File.separator + buildDirectory + File.separator + artifactId + "." + packaging;
                copyFile( buildName, artifactName, version );
            }
            //install: copy nut.xml file to local repository
            //String nutName = basedir + File.separator + "nut.xml";
            //copyFile( nutName, artifactName + ".nut", version );
*/
    }

    /**
     * Copy file from source to destination.
     * The directories up to <code>destination</code> will be
     * created if they don't already exist.
     *
     * @param source      An existing non-directory <code>File</code> to copy bytes from.
     * @param destination A non-directory <code>File</code> to write bytes to (possibly
     *                    overwriting).
     */
    public static void copyFile( final String source, final String destination )
        throws Exception
    {
        File destinationFile = new File( destination );
        File sourceFile = new File( source );
/*
        copyFile( sourceFile, destinationFile );
    }
    public static void copyFile( final File sourceFile, final File destinationFile )
        throws Exception
    {

        File destinationFile = new File( destination );
        File sourceFile = new File( source );
*/
        //check source exists
        if ( !sourceFile.exists() )
        {
            log.error( "File " + source + " does not exist" );
            throw new Exception();
        }

	try
	{
            log.debug( "   copy \'" + sourceFile.getCanonicalPath() + "\' to \'" + destinationFile.getCanonicalPath() + "\'"  );
            //does destination directory exist ?
            if ( destinationFile.getParentFile() != null && !destinationFile.getParentFile().exists() )
            {
                destinationFile.getParentFile().mkdirs();
            }
			//create FileInputStream object for source file
			FileInputStream in = new FileInputStream(sourceFile);
 			//create FileOutputStream object for destination file
			FileOutputStream out = new FileOutputStream(destinationFile);
 
			byte[] b = new byte[1024];
			int noOfBytes = 0;
			//read bytes from source file and write to destination file
			while( (noOfBytes = in.read(b)) != -1 )
			{
				out.write(b, 0, noOfBytes);
			}
			//close the streams
			in.close();
			out.close();			
 
	}
	catch(FileNotFoundException fnf)
	{
		log.error( "Specified file not found :" + fnf );
            throw new Exception();
	}
	catch(IOException ioe)
	{
		log.error( "Error while copying file :" + ioe );
            throw new Exception();
	}
        if ( sourceFile.length() != destinationFile.length() )
        {
            log.error( "Failed to copy full contents from " + source + " to " + destination );
            throw new Exception();
        }
    }

}
