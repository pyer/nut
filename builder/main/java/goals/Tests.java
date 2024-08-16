package nut.goals;

import nut.Logger;
import nut.goals.GoalException;
import nut.model.Project;
import nut.annotations.Ignore;
import nut.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
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

    private boolean noop = false;
    private int failures = 0;
    private int ignored  = 0;
    private int success  = 0;

    public void execute( Project project ) throws GoalException
    {
        if ( "zip".equals(project.getPackaging() )) {
          return;
        }

        String basedir              = project.getBaseDirectory();
        String outputDirectory      = basedir + File.separator + project.getOutputDirectory();
        String testOutputDirectory  = basedir + File.separator + project.getTestOutputDirectory();

        noop = project.noop();
        log.info("Testing");
        testingClasses(outputDirectory,testOutputDirectory);
        if (failures>0) {
          project.failure();
        }
        logResults();
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
            log.debug( "Testing: " + testOutputDirectory + " is empty" );
          } else {
            Collections.shuffle(testClasses);
            for (String test : testClasses) {
                Class<?> klass = fileToClass(cl,test);
                if ( klass == null) {
                    throw new GoalException("Cannot load class from file " + test);
                } else {
                    log.info("  - " + klass.getCanonicalName());
                    if (!noop) {
                        invokeTestMethods(klass);
                    }
                }
            }
          }
        } else {
            log.debug( "Testing: " + testOutputDirectory + " not found");
        }
    }

    private void logResults() {
        log.info("Results");
        if (success>0) {
          log.info("  - " + Integer.toString(success) + " success");
        }
        if (ignored>0) {
          log.warn("  - " + Integer.toString(ignored) + " ignored");
        }
        if (failures>0) {
          log.error("  - " + Integer.toString(failures) + " failures");
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
      }
      return result;
    }

    private void invokeTestMethods(Class<?> klass)
    {
        List<Method> methods = Arrays.asList(klass.getMethods());
        Collections.shuffle(methods);
        try {
            Object t = klass.getDeclaredConstructor().newInstance();
            for (Method method : methods) {
                //log.debug("      " + method.getName());
                if (method.isAnnotationPresent(Ignore.class)) {
                    log.warn("      " + method.getName() + ": ignored");
                    ignored++;
                } else {
                    if (method.isAnnotationPresent(Test.class)) {
                        Test test = (Test) method.getAnnotation(Test.class);
                        if (!test.enabled())
                          continue;

                        // Save current stdout and stderr
                        PrintStream out = System.out;
                        PrintStream err = System.err;
                        try {
                            if (log.isDebugEnabled()) {
                              log.debug( "      " + method.getName());
                            } else {
                              // Redirect stdout to null
                              try {
                                System.setOut(new PrintStream("/dev/null"));
                              } catch(FileNotFoundException e) {
                                e.printStackTrace();
                              }
                            }
                            // Invoke the test method
                            Object o = method.invoke(t);
                            // Restore stdout
                            System.setOut(out);
                            System.setErr(err);
                            log.trace( "      " + method.getName() + ": ok");
                            success++;
                        } catch(InvocationTargetException e) {
                            // Restore stdout
                            System.setOut(out);
                            System.setErr(err);
                            boolean expected = false;
                            Class catched = e.getCause().getClass();
                            for( Class ee : test.expectedExceptions() ) {
                              if(ee.equals(catched)) {
                                log.trace("      " + method.getName() + ": ok, catch " + catched.getName());
                                expected = true;
                              }
                            }
                            if(expected) {
                              success++;
                            } else {
                              log.error("      " + method.getName() + ": " + e.getCause().getMessage());
                              failures++;
                            }
                        }
                    }
                }
            }
        } catch(NoSuchMethodException e) {
            log.debug("NoSuchMethodException: " + e.getMessage());
        } catch(InvocationTargetException e) {
            log.debug("InvocationTargetException: " + e.getMessage());
        } catch(InstantiationException e) {
            // e.printStackTrace();
            log.debug("InstantiationException: " + e.getMessage());
        } catch(IllegalAccessException e) {
            // e.printStackTrace();
            log.debug("IllegalAccessException: " + e.getMessage());
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
