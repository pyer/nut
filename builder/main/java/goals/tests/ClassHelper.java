package nut.goals.tests;

import java.io.File;
//import java.lang.NoClassDefFoundError;
import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
import java.lang.reflect.Method;
//import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

/** Utility class for different class manipulations. */
public final class ClassHelper {

  /**
   * When given a file name to form a class name, the file name is parsed and divided into segments.
   * For example, "c:/java/classes/com/foo/A.class" would be divided into 6 segments {"C:" "java",
   * "classes", "com", "foo", "A"}.
   */

  /** Hide constructor. */
  private ClassHelper() {
    // Hide Constructor
  }

  /**
   * Returns the Class object corresponding to the given file name.
   *
   * @param file the class name.
   * @return the class corresponding to the name specified.
   */
  public static Class fileToClass(String file) {
      Class result = null;

      ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
      try {
        URL url = new URL("file://"+ file);
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

