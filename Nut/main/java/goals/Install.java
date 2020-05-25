package nut.goals;

import nut.goals.GoalException;
import nut.logging.Log;
import nut.model.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Properties;

public class Install implements Goal
{
    /** Instance logger */
    private Log log;

    public void execute( Project project ) throws GoalException
    {
        log = new Log();
        Properties pp               = project.getProperties();
        String basedir              = (String)pp.getProperty( "basedir" );
        String repository           = (String)pp.getProperty( "nut.home" );
        String mode                 = (String)pp.getProperty( "nut.mode", "SNAPSHOT" );
        if( "RELEASE".equals( mode ) )
          mode = "";
        else
          mode = "-SNAPSHOT";

        String targetDirectory      = project.getLayout().getTargetDirectory();
        String resourceDirectory    = project.getLayout().getResourceDirectory();

        String groupId              = project.getGroupId();
        String artifactId           = project.getArtifactId();
        String version              = project.getVersion()  + mode;
        String packaging            = project.getPackaging();

        log.debug( "project.groupId           = " + groupId );
        log.debug( "project.artifactId        = " + artifactId );
        log.debug( "project.version           = " + version );
        log.debug( "project.packaging         = " + packaging );

        log.info( "Installing \'" + artifactId + "\'" );
        // + "-" + version + "." + packaging
        if( "xml".equals(packaging) ) {
                String group = groupId.replace( '.', File.separatorChar );
                String artifactName = repository + File.separator + group + File.separator + artifactId + "-" + version + "." + packaging;
                String buildName = basedir + File.separator + resourceDirectory + File.separator + artifactId + "." + packaging;
                copyFile( buildName, artifactName, version );
        } else if( !"modules".equals(packaging) ) {
                String group = groupId.replace( '.', File.separatorChar );
                String artifactName = repository + File.separator + group + File.separator + artifactId + "-" + version + "." + packaging;
                //install: copy target file to local repository
                String buildName = basedir + File.separator + targetDirectory + File.separator + artifactId + "." + packaging;
                copyFile( buildName, artifactName, version );
        }
        //install: copy nut.xml file to local repository
        //String nutName = basedir + File.separator + "nut.xml";
        //copyFile( nutName, artifactName + ".nut", version );
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
    public void copyFile( final String source, final String destination, final String version )
        throws GoalException
    {
        String msg;
        File destinationFile = new File( destination );
        File sourceFile = new File( source );
        //check source exists
        if ( !sourceFile.exists() ) {
            msg = "File " + source + " does not exist";
            log.error(msg);
            throw new GoalException(msg);
        }
        //check destination exists
        if ( destinationFile.exists() && !version.endsWith("-SNAPSHOT") ) {
            msg = "File " + destination + " already exists";
            log.error(msg);
            throw new GoalException(msg);
        }

        try {
          log.debug( "   copy \'" + sourceFile.getCanonicalPath() + "\' to \'" + destinationFile.getCanonicalPath() + "\'"  );
          //does destination directory exist ?
          if( destinationFile.getParentFile() != null && !destinationFile.getParentFile().exists() ) {
            destinationFile.getParentFile().mkdirs();
          }
          //create FileInputStream object for source file
          FileInputStream in = new FileInputStream(sourceFile);
           //create FileOutputStream object for destination file
          FileOutputStream out = new FileOutputStream(destinationFile);

          byte[] b = new byte[1024];
          int noOfBytes = 0;
          //read bytes from source file and write to destination file
          while( (noOfBytes = in.read(b)) != -1 ) {
            out.write(b, 0, noOfBytes);
          }
          //close the streams
          in.close();
          out.close();

        }
        catch(FileNotFoundException fnf) {
            msg = "Specified file not found :" + fnf;
            log.error(msg);
            throw new GoalException(msg);
        }
        catch(IOException ioe) {
            msg = "Error while copying file :" + ioe;
            log.error(msg);
            throw new GoalException(msg);
        }
        if ( sourceFile.length() != destinationFile.length() ) {
            msg = "Failed to copy full contents from " + source + " to " + destination;
            log.error(msg);
            throw new GoalException(msg);
        }
    }

}
