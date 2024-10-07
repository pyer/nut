package nut.goals;

import nut.Logger;
import nut.goals.GoalException;
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
    private Logger log;
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

        log = new Logger();
        String basedir              = project.getBaseDirectory();
        String sourceDirectory      = basedir + File.separator + project.getSourceDirectory();
        String testSourceDirectory  = basedir + File.separator + project.getTestSourceDirectory();
        String outputDirectory      = basedir + File.separator + project.getOutputDirectory();
        String testOutputDirectory  = basedir + File.separator + project.getTestOutputDirectory();

        if (test) {
          log.info( "Compiling tests" );
          if (project.noop()) {
            return;
          }
          // Compiling test sources
          log.debug( testSourceDirectory );
          List testSources = sourceFiles( new File( testSourceDirectory ) );
          if ( !testSources.isEmpty() ) {
            File testOutputDir = new File( testOutputDirectory );
            if ( !testOutputDir.exists() ) {
                testOutputDir.mkdirs();
            }
            compile( testSources, testSourceDirectory, testOutputDirectory, project.getTestDependenciesClassPath(), project.getArguments() );
          }
        } else {
          log.info( "Compiling sources" );
          if (project.noop()) {
            return;
          }
          // Compiling sources
          log.debug( sourceDirectory );
          List sources = sourceFiles( new File( sourceDirectory ) );
          if ( sources.isEmpty() ) {
            log.warn( "No source code for " + project.getPath() );
          } else {
            File outputDir = new File( outputDirectory );
            if ( !outputDir.exists() ) {
                outputDir.mkdirs();
            }
            compile( sources, sourceDirectory, outputDirectory, project.getDependenciesClassPath(), project.getArguments() );
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

    private void compile(List sources, String sourceDirectory, String outputDirectory, String classPath, List<String> options) throws GoalException {
        int n = 0;
        if ( options != null) {
          n = options.size();
        }
        log.debug("      from \'" + sourceDirectory + "\' to \'" + outputDirectory + "\'" );
        log.debug("      -classpath " + classPath);
        String[] args = new String[8 + sources.size() + n];
        int i = 0;
        args[i++] = "-d";
        args[i++] = outputDirectory;
        args[i++] = "-O";
        args[i++] = "-deprecation";
        args[i++] = "-classpath";
        args[i++] = classPath;
        args[i++] = "-sourcepath";
        args[i++] = sourceDirectory;
        // optional arguments
        if ( n > 0 ) {
            for (String arg : options) {
                args[i++] = arg;
            }
        }
        // and the sources, at last
        for ( int j = 0; j < sources.size(); j++ ) {
                args[i++] = (String)(sources.get(j));
        }

        // ----------------------------------------------------------------------
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        int rc = javac.run(null, null, null, args);
        if ( rc != 0 ) {
             throw new GoalException("Compiler error " + String.valueOf(rc));
        }
    }

}
