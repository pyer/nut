package nut.goals;

import nut.Logger;
import nut.goals.GoalException;
//import nut.goals.tests.AnnotationHelper;
import nut.goals.tests.ClassHelper;
import nut.model.Project;
import nut.annotations.Ignore;
import nut.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    private void testingClasses(String testOutputDirectory)
    {
        File testClassesDir = new File(testOutputDirectory);
        if ( testClassesDir.exists() ) {
          List<String> testClasses = listOfTests(testClassesDir);
          if (testClasses.isEmpty()) {
            log.warn( testOutputDirectory + " is empty" );
          } else {
            Collections.shuffle(testClasses);
            for (String test : testClasses) {
                log.info(test);
                /*
                Class<?> klass = ClassHelper.fileToClass(test);
                if ( klass == null) {
                    log.error("Cannot load class from file " + test);
                } else {
                    log.info("Testing class " + klass.getCanonicalName());
                    invokeTestMethods(klass);
                }
                */
            }
          }
        } else {
            log.warn( "No test in " + testOutputDirectory );
        }
    }

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
