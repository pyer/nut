package nut.goals;

import nut.logging.Log;
import nut.project.Project;

import nut.testng.TestListener;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

/**
 * Goal which runs tests
 *
 * @goal test
 */
public class TestTestNG
{
    private static Log log;
    private static PrintStream sysout;
    private static PrintStream stdout;

    public static Log getLog() {
        return log;
    }

    public static PrintStream getSysOut() {
        return sysout;
    }

    public static PrintStream getStdOut() {
        return stdout;
    }

    public static void execute( Project project, Properties config )
        throws Exception
    {
        log = new Log();
        Properties pp               = project.getModel().getProperties();
        String basedir              = (String)pp.getProperty( "basedir" );
        System.setProperty( "basedir", basedir );
        String targetDirectory      = basedir + File.separator + project.getBuild().getTargetDirectory();
        String outputDirectory      = basedir + File.separator + project.getBuild().getOutputDirectory();
        String testOutputDirectory  = basedir + File.separator + project.getBuild().getTestOutputDirectory();
        String testReportDirectory  = basedir + File.separator + project.getBuild().getTestReportDirectory();

        //String testSuiteFileName    = basedir + File.separator + "test/testng.xml";
        String testSuiteFileName    = basedir + File.separator + config.getProperty("testSuiteFileName", "test/testng.xml");
        log.debug( "basedir      = " + basedir);
        log.debug( "test suite   = " + testSuiteFileName);
        log.debug( "test classes = " + testOutputDirectory);
        log.debug( "test reports = " + testReportDirectory );

        File testSuiteFile = new File( testSuiteFileName );
        if ( !testSuiteFile.exists() ) {
            log.warn( "   No test" );
        } else {
            //log.info( "   Testing " + testSuiteFileName );
            // catch stdout
            sysout = System.out;
            stdout = new PrintStream(new FileOutputStream("/dev/null"));
            System.setOut(stdout);

            URL url=new URL("file://"+outputDirectory+File.separator);
            addUrlToClassPath(url);
            url=new URL("file://"+testOutputDirectory+File.separator);
            addUrlToClassPath(url);

            TestNG tng = new TestNG();
            TestListenerAdapter tla = new nut.testng.TestListener();
            tng.addListener(tla);
            tng.setOutputDirectory( testReportDirectory );
            List<String> suites = new ArrayList<String>();
            suites.add( testSuiteFileName );
            tng.setTestSuites(suites);
            tng.run();
            // restore stdout
            System.setOut(sysout);
            if ( tng.hasFailure() ) {
              throw new Exception();
            }
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
