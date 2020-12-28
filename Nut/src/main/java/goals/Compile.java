package nut.goals;

import nut.goals.GoalException;
import nut.logging.Log;
import nut.model.Dependency;
import nut.model.Project;

import java.io.File;

import java.util.ArrayList;
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
    /** Instance logger */
    private Log log;

    public void execute(Project project, boolean noop) throws GoalException
    {
        if ( "zip".equals(project.getPackaging() )) {
          return;
        }

        log = new Log();
        String basedir              = project.getBaseDirectory();
        String sourceDirectory      = project.getSourceDirectory();
        String testSourceDirectory  = project.getTestSourceDirectory();
        String outputDirectory      = project.getOutputDirectory();
        String testOutputDirectory  = project.getTestOutputDirectory();

        log.debug( "sourceDirectory     = " + sourceDirectory );
        log.debug( "testSourceDirectory = " + testSourceDirectory );
        log.debug( "outputDirectory     = " + outputDirectory );
        log.debug( "testOutputDirectory = " + testOutputDirectory );

        if (noop) {
            log.info( "NOOP: Compiling " + sourceDirectory );
            log.info( "NOOP: Compiling " + testSourceDirectory );
            return;
        }

        /* Compiling sources */
        File outputDir = new File( basedir + File.separator + outputDirectory );
        if ( !outputDir.exists() ) {
            outputDir.mkdirs();
        }

        log.debug( "Scanning sources in " + testSourceDirectory );
        List sources = sourceFiles( new File( basedir + File.separator + sourceDirectory ) );
        if ( sources.isEmpty() ) {
            log.warn( "No source code for " + project.getId() );
        } else {
            log.info( "Compiling " + sourceDirectory );
            compile( sources, basedir + File.separator + sourceDirectory, basedir + File.separator + outputDirectory,
                     project.getDependenciesClassPath() );
        }

        /* Compiling test sources */
        File testOutputDir = new File( basedir + File.separator + testOutputDirectory );
        if ( !testOutputDir.exists() ) {
            testOutputDir.mkdirs();
        }

        log.debug( "Scanning tests in " + testSourceDirectory );
        List testSources = sourceFiles( new File( basedir + File.separator + testSourceDirectory ) );
        if ( !testSources.isEmpty() ) {
            log.info( "Compiling " + testSourceDirectory );
            compile( testSources, basedir + File.separator + testSourceDirectory, basedir + File.separator + testOutputDirectory,
                     project.getTestDependenciesClassPath() );
        }

    }

    private List<String> sourceFiles( File sourceDir )
    {
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

    private void compile(List sources, String sourceDirectory, String outputDirectory, String classPath)
        throws GoalException
    {
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
