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
    public void execute( Project project, boolean noop ) throws GoalException
    {
        if ( "zip".equals(project.getPackaging() )) {
          return;
        }

        Log log = new Log();
        String basedir              = project.getBaseDirectory();
        String testOutputDirectory  = basedir + File.separator + project.getTestOutputDirectory();
        String testReportDirectory  = basedir + File.separator + project.getTestReportDirectory();
        log.debug( "test classes = " + testOutputDirectory);
        log.debug( "test reports = " + testReportDirectory);

        if (noop) {
            log.info( "NOOP: Testing " + testOutputDirectory );
            return;
        }

        File testClassesDir = new File(testOutputDirectory);
        if ( testClassesDir.exists() ) {
            log.info( "Testing " + testOutputDirectory );
            String command   = System.getProperty( "java.home", "/usr" ) + "/bin/java";
            String classpath = project.getTestDependenciesClassPath();
            log.debug("classpath = " + classpath);

            String testClasses = listOfTests(testClassesDir, testOutputDirectory.length() + 1);
            log.debug("testclass = " + testClasses);
            if (testClasses.isEmpty()) {
                log.warn( testOutputDirectory + " is empty" );
                return;
            }

            ProcessBuilder pb = new ProcessBuilder(command, "-cp", classpath, "-Dbasedir=" + basedir,
                                                  "org.testng.TestNG", "-d", testReportDirectory, "-testclass", testClasses);
            pb.inheritIO();
            int returnCode = 0;
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
                throw new GoalException("At least one test failed, see file://" + testReportDirectory + "/index.html" );
            }
        } else {
            log.warn( "No test for " + project.getId() );
        }
    }

    private static String listOfTests( File rootDir, int begin )
    {
        String tests = "";
        if( rootDir.exists() ) {
          for (String test : rootDir.list()) {
            File child = new File(rootDir, test);
            if (child.isDirectory()) {
                tests = tests + listOfTests( child, begin );
            } else {
                String childPath = child.getAbsolutePath();
                if ( childPath.endsWith(".class") ) {
                    tests = tests + "," + childPath;
                }
            }
          }
        }
        return tests;
    }

}
