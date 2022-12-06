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

//        Logger log = new Logger();
        String basedir              = project.getBaseDirectory();
        String testOutputDirectory  = basedir + File.separator + project.getTestOutputDirectory();

        if (project.noop()) {
            log.info( "NOOP: Testing " + testOutputDirectory );
        } else {
            log.info( "Testing " + testOutputDirectory );
            testingClasses(testOutputDirectory);
        }
    }

    private void testingClasses(String testOutputDirectory) throws GoalException
    {
        File testClassesDir = new File(testOutputDirectory);
        if ( testClassesDir.exists() ) {
          List<String> testClasses = listOfTests(testClassesDir);
          if (testClasses.isEmpty()) {
            log.warn( testOutputDirectory + " is empty" );
          } else {
            Collections.shuffle(testClasses);
            int index = testOutputDirectory.length();
            for (String test : testClasses) {
                String display = "  - " + test.substring(index);
                log.info(display);
                Class<?> klass = fileToClass(test);
                if ( klass == null) {
                    throw new GoalException("Cannot load class from file " + test);
                } else {
                    log.info("Testing class " + klass.getCanonicalName());
                    invokeTestMethods(klass);
                }
            }
          }
        } else {
            log.warn( "No test in " + testOutputDirectory );
        }
    }


  /**
   * Returns the Class object corresponding to the given file name.
   * When given a file name to form a class name, the file name is parsed and divided into segments.
   * For example, "c:/java/classes/com/foo/A.class" would be divided into 6 segments {"C:" "java",
   * "classes", "com", "foo", "A"}.
   *
   * @param file the class name.
   * @return the class corresponding to the name specified.
   */
  private Class fileToClass(String file) {
      Class result = null;

      ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
      try {
        URL url = new URL("file://"+ file);
        log.debug( "fileToClass: url=" + url.toString());
        URL[] urls = { url };
//        URLClassLoader ucl = new URLClassLoader(classUrls);


        // Create class loader using given codebase
        // Use prevCl as parent to maintain current visibility
        //ClassLoader ucl = URLClassLoader.newInstance(urls, this.getClass().getClassLoader());
        ClassLoader ucl = URLClassLoader.newInstance(urls, prevCl);

        // Save class loader so that we can restore later
        Thread.currentThread().setContextClassLoader(ucl);

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
            log.debug("fileToClass: class=" + className);
            result = ucl.loadClass(className);
          } catch(NoClassDefFoundError e) {
	          //e.printStackTrace();
            result = null;
          } catch(ClassNotFoundException e) {
	          //e.printStackTrace();
            result = null;
          } catch(Exception e) {
	          e.printStackTrace();
            result = null;
          }
          i--;
          className = segments[i] + "." + className;
        } while (i > 0 && result == null);

      } catch(MalformedURLException e) {
	      e.printStackTrace();
      } catch(Exception e) {
	      e.printStackTrace();
        result = null;
//      } catch (NamingException e) {
//        e.printStackTrace();
      } finally {
        // Restore
        Thread.currentThread().setContextClassLoader(prevCl);
      }
      return result;
  }

/*
    String url = args[0];
    ClassLoader prevCl = Thread.currentThread().getContextClassLoader();

    // Create class loader using given codebase
    // Use prevCl as parent to maintain current visibility
    ClassLoader urlCl = URLClassLoader.newInstance(new URL[]{new URL(url)}, prevCl);

        try {
        // Save class loader so that we can restore later
            Thread.currentThread().setContextClassLoader(urlCl);

        // Expect that environment properties are in
        // application resource file found at "url"
        Context ctx = new InitialContext();

        System.out.println(ctx.lookup("tutorial/report.txt"));

        // Close context when no longer needed
        ctx.close();
    } catch (NamingException e) {
        e.printStackTrace();
        } finally {
            // Restore
            Thread.currentThread().setContextClassLoader(prevCl);
        }
    }
*/

    private void invokeTestMethods(Class<?> klass)
    {
        List<Method> methods = Arrays.asList(klass.getMethods());
        Collections.shuffle(methods);
        try {
            Object t = klass.newInstance();
            for (Method method : methods) {
                log.debug("  - " + method.getName());
                if (method.isAnnotationPresent(Test.class) && ! method.isAnnotationPresent(Ignore.class)) {
                    log.warn("@Test");
                    Object o = method.invoke(t);
                }
            }
        } catch(InvocationTargetException e) {
            e.printStackTrace();
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
