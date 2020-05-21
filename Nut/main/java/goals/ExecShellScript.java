package nut.goals;

import nut.goals.GoalException;
import nut.logging.Log;
import nut.project.Project;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

/**
 * Goal which runs script
 *
       <goal>
         <name>???</name>
         <class>ExecShellScript</class>
         <scriptFile>src/script/xxx</scriptFile>
       </goal>
 *
 * @goal test
 */
public class ExecShellScript
{
    public static void execute( Project project )
        throws GoalException
    {
        Log log = new Log();
        Properties pp          = project.getProperties();
        String basedir         = (String)pp.getProperty( "basedir" );
        String outputDirectory = basedir + File.separator + project.getLayout().getOutputDirectory();
        //String scriptFile      = basedir + File.separator + config.getProperty("scriptFile", "");
        String scriptFile      = basedir + File.separator + "script";
        log.debug( "basedir = " + basedir);
        log.debug( "script  = " + scriptFile);

        ProcessBuilder pb = new ProcessBuilder("/bin/sh", scriptFile);
        pb.inheritIO();
        try {
            Process proc = pb.start();
            if (proc.waitFor() != 0) {;
                throw new GoalException("'" + scriptFile + "' failed !");
            }
        }
        catch(IOException e) {
            throw new GoalException(e.getMessage());
        }
        catch(Exception e) {
            throw new GoalException(e.getMessage());
        }
    }
}
