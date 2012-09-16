package nut.plugins;

import nut.logging.Log;
import nut.project.NutProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.Properties;

public class installer
{
    /** Instance logger */
    private static Log log;

    public static void execute( NutProject project )
        throws Exception
    {
        log =new Log();
        Properties pluginProperties = project.getModel().getProperties();
        String basedir        = (String)pluginProperties.getProperty( "basedir" );
        String buildDirectory = (String)pluginProperties.getProperty( "build.directory" );
        String artifactId          = (String)pluginProperties.getProperty( "project.artifactId" );
        String version             = (String)pluginProperties.getProperty( "project.version" );
        String packaging           = (String)pluginProperties.getProperty( "project.packaging" );

        String artifactPath        = (String)pluginProperties.getProperty( "project.artifactPath" );

        log.debug( "basedir                   = " + basedir );
        log.debug( "build.directory           = " + buildDirectory );
        log.debug( "project.artifactId        = " + artifactId );
        log.debug( "project.version           = " + version );
        log.debug( "project.packaging         = " + packaging );
        log.debug( "project.artifactPath      = " + artifactPath );

            log.info( "   Installing \'" + artifactId + "\'" );
            // + "-" + version + "." + packaging
            if( !packaging.equals("modules") )
            {
                //install: copy target file to local repository
                String artifactName = basedir + File.separator + buildDirectory + File.separator + artifactId + "." + packaging;
                copyFile( artifactName, artifactPath, version );
            }
            //install: copy nut.xml file to local repository
            String nutName = basedir + File.separator + "nut.xml";
            copyFile( nutName, artifactPath + ".nut", version );
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
    public static void copyFile( final String source, final String destination, final String version )
        throws Exception
    {
        File destinationFile = new File( destination );
        File sourceFile = new File( source );
        //check source exists
        if ( !sourceFile.exists() )
        {
            log.error( "File " + source + " does not exist" );
            throw new Exception();
        }
        //check destination exists
        if ( destinationFile.exists() )
        {
            if( !version.endsWith("-SNAPSHOT") )
            {
                log.error( "File " + destination + " already exists" );
                throw new Exception();
            }
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
