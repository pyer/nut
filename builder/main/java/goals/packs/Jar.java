package nut.goals.packs;

import nut.Logger;
import nut.goals.GoalException;
import nut.goals.packs.util.CopyFiles;
import nut.model.Project;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class Jar
{
    /** Instance logger */
    private Logger log;
    private String name;

    // ==========================================================================
    public Jar( String name )
    {
        this.name = name;
        log = new Logger();
    }

    // ==========================================================================
    public void archive(Project project) throws GoalException
    {
        String basedir              = project.getBaseDirectory();
        String resourceDirectory    = basedir + File.separator + project.getResourceDirectory();
        String outputDirectory      = basedir + File.separator + project.getOutputDirectory();

        log.debug( "resourceDirectory   = " + resourceDirectory );
        log.debug( "outputDirectory     = " + outputDirectory );

        File outputDir = new File( outputDirectory );
        if ( !outputDir.exists() ) {
            throw new GoalException("No class files found in \'" + outputDirectory + "\'");
        }

        // ----------------------------------------------------------------------
        // Copy resource to output
        File resourceDir = new File( resourceDirectory );
        if ( resourceDir.exists() ) {
          log.debug("copy " + resourceDirectory + " to " + outputDirectory);
          try {
            CopyFiles copyResource = new CopyFiles(resourceDirectory, outputDirectory);
            copyResource.process();
          } catch ( IOException e ) {
            throw new GoalException(e.getMessage());
          }
        }
        // ----------------------------------------------------------------------
        // Create jar
        List<String> args = new ArrayList<String>();
        args.add( "jar" );
        args.add( "cf" );
        args.add( name );
        args.add( "-C" );
        args.add( outputDirectory );
        args.add( "." );
        log.debug( "jar cf " + name + " -C " + outputDirectory + ".");
        // ----------------------------------------------------------------------
        // build the command line
        String[] command = (String[]) args.toArray( new String[ args.size() ] );
        try {
            Process child = Runtime.getRuntime().exec(command);
            int status = child.waitFor();
            if ( status != 0 ) {
                throw new GoalException("Error in child process");
            }
        } catch ( InterruptedException e ) {
            throw new GoalException(e.getMessage());
        } catch ( IOException e ) {
            throw new GoalException(e.getMessage());
        }
        // ----------------------------------------------------------------------
    }
}
