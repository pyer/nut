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
         <class>Script</class>
         <configuration>
           <command>/bin/sh</command>
           <script>src/script/xxx</script>
         </configuration>
       </goal>
 *
 * @goal test
 */
public class Script
{
    public static void execute( Project project, Properties config )
        throws GoalException
    {
        Log log = new Log();
        Properties pp          = project.getModel().getProperties();
        String basedir         = (String)pp.getProperty( "basedir" );
        String outputDirectory = basedir + File.separator + project.getBuild().getOutputDirectory();
        String command         = config.getProperty("command", "/bin/sh");
        String script          = basedir + File.separator + config.getProperty("script", "");
        log.debug( "basedir = " + basedir);
        log.debug( "command = " + command);
        log.debug( "script  = " + script);
        for ( Enumeration en = config.propertyNames(); en.hasMoreElements(); ) {
          String key = (String) en.nextElement();
          log.debug( "configuration["+key+"] = " + config.getProperty(key));
        }

        ProcessBuilder pb = new ProcessBuilder(command, script);
        pb.inheritIO();
        try {
            Process proc = pb.start();
            if (proc.waitFor() != 0) {;
                throw new GoalException("'" + command + " " + script + "' failed !");
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
