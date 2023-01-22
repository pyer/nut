package nut.goals;

import nut.Logger;
import nut.goals.GoalException;
import nut.model.Project;
import nut.annotations.Ignore;
import nut.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Goal which runs tests
 *
 * @goal test
 */
public class Tests implements Goal
{

    private Logger log = new Logger();

    public void execute( Project project ) throws GoalException
    {
        if ( "zip".equals(project.getPackaging() )) {
          return;
        }

        String basedir              = project.getBaseDirectory();
        String outputDirectory      = basedir + File.separator + project.getOutputDirectory();
        String testOutputDirectory  = basedir + File.separator + project.getTestOutputDirectory();

        if (project.noop()) {
            log.info( "NOOP: Testing " + testOutputDirectory );
        } else {
            testingClasses(outputDirectory,testOutputDirectory);
        }
    }

    private void testingClasses(String outputDirectory, String testOutputDirectory) throws GoalException
    {
        File classesDir     = new File(outputDirectory);
        File testClassesDir = new File(testOutputDirectory);
        if ( testClassesDir.exists() ) {
          URL[] urls = null;
          try {
            // convert the directories to URL format
            urls = new URL[]{testClassesDir.toURI().toURL(), classesDir.toURI().toURL()};
          } catch(MalformedURLException e) {
            log.debug("fileToClass MalformedURLException: " + e.getMessage());
	          e.printStackTrace();
          }
          // load the directories into class loader
          ClassLoader cl = new URLClassLoader(urls);

          List<String> testClasses = listOfTests(testClassesDir);
          if (testClasses.isEmpty()) {
            log.warn( "Testing: " + testOutputDirectory + " is empty" );
          } else {
            Collections.shuffle(testClasses);
            for (String test : testClasses) {
                Class<?> klass = fileToClass(cl,test);
                if ( klass == null) {
                    throw new GoalException("Cannot load class from file " + test);
                } else {
                    log.info("Testing " + klass.getCanonicalName() + " (" + test + ")");
                    invokeTestMethods(klass);
                }
            }
          }
        } else {
            log.warn( "Testing: " + testOutputDirectory + " not found");
        }
    }

    /**
     * Returns the Class object corresponding to the given file name.
     * When given a file name to form a class name, the file name is parsed and divided into segments.
     * For example, "c:/java/classes/com/foo/A.class" would be divided into 6 segments {"C:" "java",
     * "classes", "com", "foo", "A"}.
     *
     * @param cl   the class loader.
     * @param file the class name.
     * @return the class corresponding to the name specified.
     */
    private Class fileToClass(ClassLoader cl, String file) {
      Class result = null;
      try {
        // Transforms the file name into a class name.
        // Remove the ".class" extension.
        int classIndex = file.lastIndexOf(".class");
        String shortFileName = file.substring(0, classIndex);

        // Split file name into segments. For example "C:/java/classes/com/foo/A"
        String[] segments = shortFileName.split("[/\\\\]", -1);
        int i = segments.length - 1;
        String className = segments[i];
        do {
          // Try to load the class. For example "A", then "foo.A", "com.foo.A", ...
          try {
            result = cl.loadClass(className);
          } catch(NoClassDefFoundError e) {
            log.debug("fileToClass NoClassDefFoundError: " + e.getMessage());
	          //e.printStackTrace();
            result = null;
          } catch(ClassNotFoundException e) {
            log.debug("fileToClass ClassNotFoundException: " + e.getMessage());
	          //e.printStackTrace();
            result = null;
          } catch(Exception e) {
            log.debug("fileToClass Exception: " + e.getMessage());
	          e.printStackTrace();
            result = null;
          }
          i--;
          className = segments[i] + "." + className;
        } while (i > 0 && result == null);

      } catch(Exception e) {
        log.debug("fileToClass Exception: " + e.getMessage());
	      e.printStackTrace();
        result = null;
//      } catch (NamingException e) {
//        e.printStackTrace();
      }
      return result;
    }

    private void invokeTestMethods(Class<?> klass)
    {
        List<Method> methods = Arrays.asList(klass.getMethods());
        Collections.shuffle(methods);
        try {
            Object t = klass.newInstance();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Test.class) && ! method.isAnnotationPresent(Ignore.class)) {
                    log.info("  - " + method.getName());
                    Object o = method.invoke(t);
                }
            }
        } catch(InvocationTargetException e) {
            // e.printStackTrace();
            log.debug("InvocationTargetException: " + e.getMessage());
        } catch(InstantiationException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
           e.printStackTrace();
        }
    }

    private List<String> listOfTests( File rootDir )
    {
        List<String> tests = new LinkedList<String>();

        if( rootDir.exists() ) {
          for (String test : rootDir.list()) {
            File child = new File(rootDir, test);
            if (child.isDirectory()) {
                log.debug( "D - " + child.getName() );
                tests.addAll( listOfTests( child ) );
            } else {
                String childPath = child.getAbsolutePath();
                log.debug( "F - " + childPath );
                if ( childPath.endsWith(".class") ) {
                    tests.add(childPath);
                }
            }
          }
        }
        return tests;
    }

}
