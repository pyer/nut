package nut.goals;

import nut.goals.GoalException;
import nut.logging.Log;
import nut.model.Project;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Goal which runs tests
 *
 * @goal test
 */
public class Test implements Goal
{
    public void execute( Project project ) throws GoalException
    {
        int returnCode = 0;
        Log log = new Log();
        Properties pp               = project.getProperties();
        String basedir              = (String)pp.getProperty( "basedir" );
        String outputDirectory      = basedir + File.separator + project.getLayout().getOutputDirectory();
        String testOutputDirectory  = basedir + File.separator + project.getLayout().getTestOutputDirectory();
        String testReportDirectory  = basedir + File.separator + project.getLayout().getTestReportDirectory();
        String testSuiteFileName    = basedir + File.separator + project.getLayout().getTestSuite();
        log.debug( "basedir      = " + basedir);
        log.debug( "main classes = " + outputDirectory);
        log.debug( "test suite   = " + testSuiteFileName);
        log.debug( "test classes = " + testOutputDirectory);
        log.debug( "test reports = " + testReportDirectory);

        File testSuiteFile = new File( testSuiteFileName );
        if ( testSuiteFile.exists() ) {
            log.debug( "Testing " + testSuiteFileName );
            String command   = System.getProperty( "java.home", "/usr" ) + "/bin/java";
            String classpath = testOutputDirectory + ":" + outputDirectory + project.getTestDependenciesClassPath();
            log.debug("classpath = " + classpath);
            // Run a java app in a separate system process
            ProcessBuilder pb = new ProcessBuilder(command, "-cp", classpath, "-Dbasedir=" + basedir,
                                                  "nut.TestRunner", testSuiteFileName, testReportDirectory);
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
                throw new GoalException("At least one test failed !");
            }
        } else {
            log.warn( "No test for " + project.getId() );
        }
    }
}
