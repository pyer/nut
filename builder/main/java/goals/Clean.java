package nut.goals;

import nut.Logger;
import nut.goals.GoalException;
import nut.model.Project;

import java.io.File;
import java.io.IOException;

/**
 * Goal which cleans the build.
 *
 * This attempts to clean a project's working directory of the files that were generated at build-time.
 * It discovers and deletes the directories configured in clean.target property
 *
 * @goal clean
 */
public class Clean implements Goal
{
    /** Instance logger */
    private Logger log;

    /**
     * Deletes file-sets in the following project build directory order:
     * (source) directory, output directory, test directory
     *
     * @throws Exception When a directory failed to get deleted.
     */
    public void execute(Project project) throws GoalException
    {
        log = new Logger();
        String basedir = project.getBaseDirectory();
        String target  = project.getTargetDirectory();
        String fullPath = basedir + File.separator + target;

        if (project.noop()) {
          log.info( "NOOP: Cleaning " + fullPath );
        } else {
          log.info( "Cleaning " + fullPath );
          removeDirectory( new File(fullPath) );
        }
    }


    /**
     * Deletes a directory and its contents.
     *
     * @param dir The base directory of the included and excluded files.
     * @throws MojoExecutionException When a directory failed to get deleted.
     */
    private void removeDirectory( File dir ) throws GoalException
    {
        if ( dir != null ) {
            if ( !dir.exists() ) {
                //log.warn( dir + " doesn't exist" );
                return;
            }

            if ( !dir.isDirectory() ) {
                log.error( dir + " is not a directory." );
                throw new GoalException( dir + " is not a directory." );
            }

            try {
                log.debug( "   delete " + dir.getPath() );
                deleteDirectory(dir);
            } catch ( IOException e ) {
                log.error( "Failed to delete directory: " + dir + ". Reason: " + e.getMessage(), e );
                throw new GoalException(e.getMessage());
            } catch ( IllegalStateException e ) {
                log.error( "Failed to delete directory: " + dir + ". Reason: " + e.getMessage(), e );
                throw new GoalException(e.getMessage());
            }
        }
    }

    private void deleteDirectory(File path) throws IOException
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
