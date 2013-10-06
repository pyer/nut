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

    public static void execute( NutProject project, Log logger )
        throws Exception
    {
        log = logger;
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

/*
    public static void runTests1()
    {
     Class cls = Class.forName("ClassLoaderDemo");
     // returns the ClassLoader object associated with this Class
     ClassLoader cLoader = cls.getClassLoader();
    
     System.out.println(cLoader.getClass());
    
     // finds resource with the given name
     URL url = cLoader.getResource("file.txt");
     System.out.println("Value = " + url);

     // finds resource with the given name
     url = cLoader.getResource("newfolder/a.txt");
     System.out.println("Value = " + url);  
   }
*/
/*
public static ArrayList<String>getClassNamesFromPackage(String packageName) throws IOException{
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    URL packageURL;
    ArrayList<String> names = new ArrayList<String>();;

    packageName = packageName.replace(".", "/");
    packageURL = classLoader.getResource(packageName);

    // loop through files in classpath
        File folder = new File(packageURL.getFile());
        File[] contenuti = folder.listFiles();
        String entryName;
        for(File actual: contenuti){
            entryName = actual.getName();
            entryName = entryName.substring(0, entryName.lastIndexOf('.'));
            names.add(entryName);
        }
    return names;
}
*/
  private static void runTests(File path)
        throws Exception
  {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) {
               //log.debug( "   test " + path.getAbsolutePath() );
               if(files[i].isDirectory()) {
                   runTests(files[i]);
               } else {
                   runTest( files[i].getAbsolutePath() );
               }
            }
  }

  private static void runTest(String testPackage)
        throws Exception
  {
        Class<? extends TestCase> testClass = null;
// target/test-classes/nut/plugins/junitTest.class  [error] Class not found "nut.plugins.junitTest"
        // length of testPackage without ending ".class"
        int len = testPackage.length()-6;
        String suiteClassName;
        log.debug( "   file: " + testPackage );
        if( testPackage.endsWith(".class") ) {
            // Cut testOutputDirectory name and ending ".class"
            // and substitute '/' by '.'
            suiteClassName = testPackage.substring(index,len).replace( File.separatorChar, '.' );
            log.debug( "   test: " + suiteClassName );
        } else {
            throw new Exception();
        }

        try {
            ClassLoader cLoader = ClassLoader.getSystemClassLoader();
            Class lClass = cLoader.loadClass(suiteClassName);

            ////testClass = Class.forName(suiteClassName).asSubclass(TestCase.class);
            ////testClass = Class.forName(suiteClassName);
            //testClass = lClass.asSubclass(TestCase.class);
//            testClass = (TestCase) lClass;
//MyClass obj = (MyClass) Class.forName("test.MyClass").newInstance();
//obj.testmethod();
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
