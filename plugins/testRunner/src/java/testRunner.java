package nut.plugins;

import nut.logging.Log;
import nut.project.NutProject;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/*
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.Enumeration;
*/

//import org.testng.TestListenerAdapter;
import org.testng.TestNG;

/**
 * Goal which runs tests
 *
 * @goal test
 */
public class testRunner
{
    public static void execute( NutProject project, Log log )
        throws Exception
    {
        Properties pp               = project.getModel().getProperties();
        String basedir              = (String)pp.getProperty( "basedir" );
        String buildDirectory       = project.getBuild().getDirectory();
        String testOutputDirectory  = project.getBuild().getTestOutputDirectory();
        String reportsDirectory     = buildDirectory + File.separator + "test-reports";
        // to put in a property
        //String testSuitePath        = project.getBuild().getTestOutputDirectory();
        String testSuiteFileName    = basedir + "/test/testng.xml";
        log.debug( "testdir = " + basedir + File.separator + testOutputDirectory);
        log.debug( "reports = " + basedir + File.separator + reportsDirectory );
        log.info( "   Testing " + testSuiteFileName );

        TestNG tng = new TestNG();
//        TestListenerAdapter tla = new TestListenerAdapter();
//        tng.addListener(tla);
        tng.setOutputDirectory( reportsDirectory );
        List<String> suites = new ArrayList<String>();
        suites.add( testSuiteFileName );
        tng.setTestSuites(suites);

        tng.run();
    }
}
