package nut.goals;

import nut.goals.GoalException;
import nut.logging.Log;
import nut.model.Project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal which runs application with dependencies
 *
 * @goal run
 */
public class Run implements Goal
{
    public void execute(Project project) throws GoalException
    {
        int returnCode = 0;
        List<String> command = new ArrayList<String>();

        Log log = new Log();
        if (project.getMainClass() == null) {
            throw new GoalException("mainClass is not defined");
        }

        command.add(System.getProperty( "java.home", "/usr" ) + "/bin/java");
        command.add("-cp");
        command.add(project.getDependenciesClassPath());
        command.add(project.getMainClass());

        List<String> arguments = project.getArguments();
        if ( arguments != null && arguments.size() > 0 ) {
            for (String arg : arguments) {
                command.add(arg);
            }
        }

        if (project.noop()) {
            log.info( "NOOP: Running" );
            log.info(String.join(" ", command));
            return;
        }

        // Run a java app in a separate system process
        ProcessBuilder pb = new ProcessBuilder(command);
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
