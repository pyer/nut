package nut.goals;

import nut.goals.GoalException;
import nut.logging.Log;
import nut.model.Dependency;
import nut.model.Project;

import java.io.File;

import java.util.LinkedList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * Compiles application sources
 *
 */
public class Compile implements Goal
{
    private Log log;
    private boolean test;

    public Compile() {
      test = false;
    }

    public Compile(String mode) {
      if ("test".equals(mode)) {
        test = true;
      }
    }

    public void execute(Project project) throws GoalException {
        if ( "zip".equals(project.getPackaging() )) {
          return;
        }

        log = new Log();
        String basedir              = project.getBaseDirectory();
        String sourceDirectory      = basedir + File.separator + project.getSourceDirectory();
        String testSourceDirectory  = basedir + File.separator + project.getTestSourceDirectory();
        String outputDirectory      = basedir + File.separator + project.getOutputDirectory();
        String testOutputDirectory  = basedir + File.separator + project.getTestOutputDirectory();

        log.debug( "sourceDirectory     = " + sourceDirectory );
        log.debug( "testSourceDirectory = " + testSourceDirectory );
        log.debug( "outputDirectory     = " + outputDirectory );
        log.debug( "testOutputDirectory = " + testOutputDirectory );

        if (test) {
          if (project.noop()) {
            log.info( "NOOP: Compiling " + testSourceDirectory );
            return;
          }
          // Compiling test sources
          log.debug( "Scanning tests in " + testSourceDirectory );
          List testSources = sourceFiles( new File( testSourceDirectory ) );
          if ( !testSources.isEmpty() ) {
            File testOutputDir = new File( testOutputDirectory );
            if ( !testOutputDir.exists() ) {
                testOutputDir.mkdirs();
            }
            log.info( "Compiling " + testSourceDirectory );
            compile( testSources, testSourceDirectory, testOutputDirectory, project.getTestDependenciesClassPath() );
          }
        } else {
          if (project.noop()) {
            log.info( "NOOP: Compiling " + sourceDirectory );
            return;
          }
          // Compiling sources
          log.debug( "Scanning sources in " + sourceDirectory );
          List sources = sourceFiles( new File( sourceDirectory ) );
          if ( sources.isEmpty() ) {
            log.warn( "No source code for " + project.getId() );
          } else {
            File outputDir = new File( outputDirectory );
            if ( !outputDir.exists() ) {
                outputDir.mkdirs();
            }
            log.info( "Compiling " + sourceDirectory );
            compile( sources, sourceDirectory, outputDirectory, project.getDependenciesClassPath() );
          }
        }
    }

    private List<String> sourceFiles( File sourceDir ) {
        List<String> sources = new LinkedList<String>();
        if( !sourceDir.exists() )
            return sources;

        String[] sourcesList = sourceDir.list();
        log.debug( "Source directory is " + sourceDir.getPath() );

        for (int i=0; i<sourcesList.length; i++) {
            File child = new File(sourceDir, sourcesList[i]);
            if (child.isDirectory()) {
                sources.addAll( sourceFiles( child ) );
            } else {
                if ( child.getName().endsWith(".java") ) {
                     log.debug( "- " + child.getPath() );
                     sources.add( child.getPath() );
                }
            }

        }
        return sources;
    }

    private void compile(List sources, String sourceDirectory, String outputDirectory, String classPath) throws GoalException {
        int n = 8 + sources.size();
        log.debug("      from \'" + sourceDirectory + "\' to \'" + outputDirectory + "\'" );
        log.debug("      -classpath " + classPath);
        String[] args = new String[n];
        args[0] = "-d";
        args[1] = outputDirectory;
        args[2] = "-O";
        //    args[]( "-verbose" );
        args[3] = "-deprecation";
        args[4] = "-classpath";
        args[5] = classPath;
        args[6] = "-sourcepath";
        args[7] = sourceDirectory;
        // and the sources, at last
        for ( int i = 0; i < sources.size(); i++ ) {
            args[8+i] = (String)(sources.get(i));
        }

        // ----------------------------------------------------------------------
        /*
        for ( int i=0; i<n; i++ ) {
            log.debug( "  '"+args[i]+"'" );
        }
        */
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        int rc = javac.run(null, null, null, args);
        if ( rc != 0 ) {
             throw new GoalException("Compiler error " + String.valueOf(rc));
        }
    }

}
