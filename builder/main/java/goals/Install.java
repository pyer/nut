package nut.goals;

import nut.Logger;
import nut.goals.GoalException;
import nut.model.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Install implements Goal
{
    /** Instance logger */
    private Logger log;

    public void execute(Project project) throws GoalException
    {
        log = new Logger();

        String repository           = project.getRepository();
        String basedir              = project.getBaseDirectory();
        String targetDirectory      = project.getTargetDirectory();
        String resourceDirectory    = project.getResourceDirectory();

        String groupId              = project.getGroup();
        String artifactId           = project.getName();
        String version              = project.getVersion();
        String packaging            = project.getPackaging();

        String group = groupId.replace( '.', File.separatorChar );
        String artifactName = repository + File.separator + group + File.separator + artifactId + "-" + version + "." + packaging;

        log.info( "Installing" );
        log.debug( "Artifact \'" + artifactName + "\'" );

        if (project.noop()) {
          return;
        }

        if( "modules".equals(packaging) ) {
          log.debug( "No install for modules" );
          return;
        }

        if( "dir".equals(packaging) ) {
          log.debug( "No install for packaging 'dir'" );
          return;
        }

        //install: copy target file to local repository
        String buildName = basedir + File.separator + targetDirectory + File.separator + artifactId + "." + packaging;
        if ( new File(buildName).isDirectory() ) {
          log.debug( "'" + buildName + "' is a directory and is not installed" );
          return;
        }

        //install: copy target file to local repository
        copyFile( buildName, artifactName, version );
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
        File destinationFile = new File( destination );
        File sourceFile = new File( source );
        //check source exists
        if ( !sourceFile.exists() ) {
            throw new GoalException("File " + source + " does not exist");
        }
        //check destination exists
        if ( destinationFile.exists() && !version.endsWith("-SNAPSHOT") ) {
            throw new GoalException("File " + destination + " already exists");
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
            throw new GoalException("Specified file not found :" + fnf);
        }
        catch(IOException ioe) {
            throw new GoalException("Error while copying file :" + ioe);
        }
        if ( sourceFile.length() != destinationFile.length() ) {
            throw new GoalException("Failed to copy full contents from " + source + " to " + destination);
        }
    }

}
