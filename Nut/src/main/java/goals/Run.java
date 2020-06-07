package nut.goals;

import nut.goals.GoalException;
import nut.logging.Log;
import nut.model.Project;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Goal which runs application with dependencies
 *
 * @goal run
 */
public class Run implements Goal
{
    public void execute( Project project ) throws GoalException
    {
        int returnCode = 0;
        Log log = new Log();
        Properties pp               = project.getProperties();
        String basedir              = (String)pp.getProperty( "basedir" );
        String mainClass            = (String)pp.getProperty( "mainClass" );
        String targetDirectory      = basedir + File.separator + project.getLayout().getTargetDirectory();
        if (mainClass == null) {
            throw new GoalException("mainClass property is not defined");
        }

        String command   = System.getProperty( "java.home", "/usr" ) + "/bin/java";
        String jar = targetDirectory + File.separator + project.getArtifactId() + "." + project.getPackaging();
        log.debug( "Running " + jar );
        String classpath = jar + project.getDependenciesClassPath();
        log.debug("classpath  = " + classpath);
        log.debug("main class = " + mainClass);
        // Run a java app in a separate system process
        ProcessBuilder pb = new ProcessBuilder(command, "-cp", classpath, mainClass);
        pb.inheritIO();
        try {
            Process proc = pb.start();
            returnCode = proc.waitFor();
        } catch(IOException e) {
            throw new GoalException(e.getMessage());
        } catch(NullPointerException e) {
            throw new GoalException(e.getMessage());
        } catch(IndexOutOfBoundsException e) {
            throw new GoalException(e.getMessage());
        } catch(SecurityException e) {
            throw new GoalException(e.getMessage());
        } catch(Exception e) {
            throw new GoalException(e.getMessage());
        }
        if (returnCode!=0) {
            throw new GoalException("Application failed");
        }
    }
}
