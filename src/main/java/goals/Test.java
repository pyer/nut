package nut.goals;

/* *****************************
 * Execute tests for nut project
 * *****************************

The command built in this class
-------------------------------
java
-cp classpath
-Dbasedir=basedir
org.testng.TestNG
-d testReportDirectory
-suitename project.getName()
-testclass testClasses.substring(1)


The real command executed on my PC
----------------------------------
/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java
-cp /home/pba/GitHub/nut/target/classes:/home/pba/nutRepository/org/codehaus/plexus/plexus-utils-3.0.jar:/home/pba/GitHub/nut/target/test-classes:/home/pba/nutRepository/com/beust/jcommander-1.48.jar:/home/pba/nutRepository/org/testng/testng-6.8.7.jar
-Dbasedir=/home/pba/GitHub/nut
org.testng.TestNG
-d /home/pba/GitHub/nut/target/test-reports
-suitename nut
-testclass /home/pba/GitHub/nut/target/test-classes/nut/model/ProjectTest.class,/home/pba/GitHub/nut/target/test-classes/nut/model/DependencyTest.class,/home/pba/GitHub/nut/target/test-classes/nut/logging/LogTest.class,/home/pba/GitHub/nut/target/test-classes/nut/build/SorterTest.class,/home/pba/GitHub/nut/target/test-classes/nut/build/ScannerTest.class,/home/pba/GitHub/nut/target/test-classes/nut/build/DependencyCheckerTest.class,/home/pba/GitHub/nut/target/test-classes/nut/goals/CleanTest.class,/home/pba/GitHub/nut/target/test-classes/nut/goals/InstallTest.class

 ******************************* */

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
        if ( "zip".equals(project.getPackaging() )) {
          return;
        }

        Log log = new Log();
        String basedir              = project.getBaseDirectory();
        String testOutputDirectory  = basedir + File.separator + project.getTestOutputDirectory();
        String testReportDirectory  = basedir + File.separator + project.getTestReportDirectory();
        log.debug( "test classes = " + testOutputDirectory);
        log.debug( "test reports = " + testReportDirectory);

        if (project.noop()) {
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
                                                  "org.testng.TestNG", "-d", testReportDirectory,
                                                  "-suitename", project.getName(),
                                                  "-testclass", testClasses.substring(1));
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
