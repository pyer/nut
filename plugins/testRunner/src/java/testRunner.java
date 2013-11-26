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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import java.net.URL;
import java.net.URLClassLoader;
//import java.ClassLoader;


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

        File testSuiteFile = new File( testSuiteFileName );
        if ( !testSuiteFile.exists() )
        {
            log.warn( "   No test" );
        }
        else
        {
            log.info( "   Testing " + testSuiteFileName );

            URL url=new URL("file://"+basedir+File.separator+testOutputDirectory+File.separator);
            addUrlToClassPath(url);

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

    /**
    * Adds the content pointed by the URL to the classpath during runtime.
    * Uses reflection since <code>addURL</code> method of
    * <code>URLClassLoader</code> is protected.
    */
    private static void addUrlToClassPath(URL url) {
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            Method m = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            m.setAccessible(true);
            m.invoke(classLoader, new Object[]{url});

        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to add URL: " + url, ex);
        }
    }
}
