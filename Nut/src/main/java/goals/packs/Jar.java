package nut.goals.packs;

import nut.goals.GoalException;
import nut.logging.Log;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class Jar
{
    /** Instance logger */
    private Log log;

    private String name;
    // ==========================================================================
    public Jar( String name )
    {
        this.name = name;
        log = new Log();
    }

    // ==========================================================================
    public void archive(String directory) throws GoalException
    {
        log.info( "Creating \'" + this.name + "\'" );
        // ----------------------------------------------------------------------
        List<String> args = new ArrayList<String>();
        args.add( "jar" );
        args.add( "cf" );
        args.add( name );
        args.add( "-C" );
        args.add( directory );
        args.add( "." );
        log.debug( "jar cf " + name + " -C " + directory + ".");
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
    // ==========================================================================
}
