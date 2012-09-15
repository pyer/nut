package nut.plugins;

import nut.logging.Log;

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

    public static void execute(Properties pluginContext, List dependencies, List testDependencies )
        throws Exception
    {
        log =new Log();
        String basedir             = (String)pluginContext.getProperty( "basedir" );
        String buildDirectory      = (String)pluginContext.getProperty( "build.directory" );
        String sourceDirectory     = (String)pluginContext.getProperty( "build.sourceDirectory" );
        String testSourceDirectory = (String)pluginContext.getProperty( "build.testSourceDirectory" );
        String outputDirectory     = (String)pluginContext.getProperty( "build.outputDirectory" );
        String testOutputDirectory = (String)pluginContext.getProperty( "build.testOutputDirectory" );
        String artifactId          = (String)pluginContext.getProperty( "project.artifactId" );
        String packaging           = (String)pluginContext.getProperty( "project.packaging" );

        /*
        log.debug( "build.directory           = " + buildDirectory );
        log.debug( "build.sourceDirectory     = " + sourceDirectory );
        log.debug( "build.testSourceDirectory = " + testSourceDirectory );
        log.debug( "build.outputDirectory     = " + outputDirectory );
        log.debug( "build.testOutputDirectory = " + testOutputDirectory );
        log.debug( "project.artifactId        = " + artifactId );
        */
        
        File directoryPath = new File( basedir + File.separator + testOutputDirectory );
        index = 1 + directoryPath.getPath().length();
        //log.debug( "Testing " + directoryPath.getPath() );

        log.info( "   Testing..." );
/*
        for ( int i = 0; i < testDependencies.size(); i++ )
        {
            String dep = (String)(testDependencies.get(i));
            log.debug( "testDependency " + dep );
            copyFile( dep, testOutputDirectory + File.separator + "junit.jar" );
        }
*/

        runDirectory( directoryPath );
    }

    /**
     * Runs all test classes in a directory and its sub folders.
     *
     */
    public static void runDirectory( File dir )
        throws Exception
    {
        if ( dir != null )
        {
            if ( !dir.exists() )
            {
                //log.warn( dir + " doesn't exist" );
                return;
            }

            if ( dir.isDirectory() )
            {
                //log.warn( "   run tests in " + dir.getAbsolutePath() );
                runTests(dir);
            }
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
