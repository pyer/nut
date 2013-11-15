package nut.plugins;

import nut.logging.Log;
import nut.project.NutProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
//import org.junit.runner.notification.RunListener;

/**
 * Goal which runs JUnit tests
 * org.junit.runner.JUnitCore.runClasses(TestClass1.class, ...);
 *
 * @goal test
 */
public class junitRunner
{
/*
    static class TextListener extends RunListener
    {
      // Instance logger
      private static Log log;

      TextListener(Log logger) {
        log = logger;
      }
      public void testFailure(Failure failure) {
        //log.error(failure.toString());
        log.error(failure.toString());
      }
    }
*/

    public static void execute( NutProject project, Log log )
        throws Exception
    {
        Properties pluginProperties = project.getModel().getProperties();
        String basedir              = (String)pluginProperties.getProperty( "basedir" );
        String testOutputDirectory  = (String)pluginProperties.getProperty( "build.testOutputDirectory" );
        log.debug("basedir=" + basedir);
        log.debug("testdir=" + testOutputDirectory);
/*
        File directoryPath = new File( basedir + File.separator + testOutputDirectory );
*/
      try {
        //URL[] url={new URL("file://"+basedir+File.separator+testOutputDirectory+File.separator)};
        //URLClassLoader loader = new URLClassLoader(url);
        JUnitCore junit = new JUnitCore();
        //junit.addListener( new JunitRunner.TextListener(log) );
        log.info("   Testing" );
        Result result = junit.run(Class.forName("TestSuite"));
        //Result result = junit.run(loader.loadClass("junit.framework.TestSuite"));
        log.info("   ==> " + result.getRunCount()+" tests" );
        log.info("   ==> " + result.getIgnoreCount()+" ignores" );
        log.info("   ==> " + result.getFailureCount()+" failures" );
        if( result.getFailureCount()>0 ) {
            List<Failure> fList = result.getFailures();
            for (Failure f : fList) {
                log.error(f.toString());
            }
            throw new Exception();
        }
      }
      catch( ClassNotFoundException e ) {
        log.warn("   Test suite is empty" );
      }
    }
}
