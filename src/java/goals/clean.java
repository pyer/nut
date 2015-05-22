package nut.goals;

import nut.logging.Log;
import nut.project.NutProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.Properties;

/**
 * Goal which cleans the build.
 *
 * <P>This attempts to clean a project's working directory of the files that
 * were generated at build-time. By default, it discovers and deletes the
 * directories configured in <code>project.build.directory</code>,
 * <code>project.build.outputDirectory</code>,
 * <code>project.build.testOutputDirectory</code>, and
 * <code>project.reporting.outputDirectory</code>. </P>
 *
 * @goal clean
 */
public class clean
{
    /** Instance logger */
    private static Log log;

    /**
     * Deletes file-sets in the following project build directory order:
     * (source) directory, output directory, test directory
     *
     * @throws Exception When a directory failed to get deleted.
     */
    public static void execute( NutProject project, Log logger )
        throws Exception
    {
        log = logger;
        Properties pp  = project.getModel().getProperties();
        String basedir = (String)pp.getProperty( "basedir" );
        String buildDirectory = project.getBuild().getDirectory();
        
        File directoryPath = new File( basedir + File.separator + buildDirectory );
        log.info( "   Cleaning " + directoryPath.getPath() );
        removeDirectory( directoryPath );
    }


    /**
     * Deletes a directory and its contents.
     *
     * @param dir The base directory of the included and excluded files.
     * @throws MojoExecutionException When a directory failed to get deleted.
     */
    private static void removeDirectory( File dir )
        throws Exception
    {
        if ( dir != null )
        {
            if ( !dir.exists() )
            {
                //log.warn( dir + " doesn't exist" );
                return;
            }

            if ( !dir.isDirectory() )
            {
                log.error( dir + " is not a directory." );
                throw new Exception();
            }

            try
            {
                log.debug( "   delete " + dir.getAbsolutePath() );
                deleteDirectory(dir);
            }
            catch ( IOException e )
            {
                log.error( "Failed to delete directory: " + dir + ". Reason: " + e.getMessage(), e );
                throw new Exception();
            }
            catch ( IllegalStateException e )
            {
                log.error( "Failed to delete directory: " + dir + ". Reason: " + e.getMessage(), e );
                throw new Exception();
            }
        }
    }


  private static void deleteDirectory(File path)
        throws IOException
  {
    if( path.exists() ) {
      File[] files = path.listFiles();
      for(int i=0; i<files.length; i++) {
         if(files[i].isDirectory()) {
           deleteDirectory(files[i]);
         }
         else {
           files[i].delete();
         }
      }
    }
    path.delete();
  }

}