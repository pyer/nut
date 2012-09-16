package nut.plugins;

import nut.logging.Log;
import nut.project.NutProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Goal which runs JUnit tests
 *
 * @goal test
 */
public class junitRunner
{
    /** Instance logger */
    private static Log log;
    
    private static int index = 0;

    public static void execute( NutProject project )
        throws Exception
    {
        log =new Log();
        Properties pluginProperties = project.getModel().getProperties();
        String basedir              = (String)pluginProperties.getProperty( "basedir" );
        String testOutputDirectory  = (String)pluginProperties.getProperty( "build.testOutputDirectory" );

        File directoryPath = new File( basedir + File.separator + testOutputDirectory );
        index = 1 + directoryPath.getPath().length();

        // Runs all test classes in a directory and its sub folders.
        log.info( "   Testing..." );
        if ( directoryPath != null && directoryPath.isDirectory() ) {
           runTests(directoryPath);
        }
    }

  private static void runTests(File path)
        throws Exception
  {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) {
               //log.debug( "   test " + path.getAbsolutePath() );
               if(files[i].isDirectory()) {
                   runTests(files[i]);
               }
               else {
                   String testPackage = files[i].getAbsolutePath();
                   // length of testPackage without ending ".class"
                   int len = testPackage.length()-6;
                   log.debug( "   file: " + testPackage );
                   if( testPackage.endsWith(".class") ) {
                       // Cut testOutputDirectory name and ending ".class"
                       // and substitute '/' by '.'
                       String testCaseName = testPackage.substring(index,len).replace( File.separatorChar, '.' );
                       //log.debug( "   test: " + testCaseName );
                       runTest( testCaseName );
                   }
               }
            }
  }

  private static void runTest(String suiteClassName)
        throws Exception
  {
        Class<? extends TestCase> testClass = null;
        try {
            testClass = Class.forName(suiteClassName).asSubclass(TestCase.class);
        } catch (ClassNotFoundException e) {
            String clazz= e.getMessage();
            if (clazz == null)
                clazz= suiteClassName;
            log.error("Class not found \""+clazz+"\"");
            return;
        } catch(Exception e) {
            log.error("Error: "+e.toString());
            return;
        }
        Test suite = new TestSuite(testClass);
            
        TestResult result = new TestResult();
            //result.addListener(fPrinter);
            //long startTime= System.currentTimeMillis();
        suite.run(result);
            //long endTime= System.currentTimeMillis();
            //long runTime= endTime-startTime;
            //fPrinter.print(result, runTime);

        log.info("     " + suiteClassName + " : " + result.runCount()+" tests" );
        if( result.failureCount()>0 ) {
           log.info("     " + suiteClassName + " : " + result.failureCount()+" failures");
           Enumeration e = result.failures();
           while (e.hasMoreElements()) {
                log.error(e.nextElement().toString());
           }
        }
        if( result.errorCount()>0 ) {
           log.info("     " + suiteClassName + " : " + result.errorCount()+" errors");
           Enumeration e = result.errors();
           while (e.hasMoreElements()) {
                log.error(e.nextElement().toString());
           }
        }
        if (!result.wasSuccessful()) {
            throw new Exception();
        }
  }

}
