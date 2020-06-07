package nut.goals;

import nut.logging.Log;
import nut.goals.GoalException;
import nut.model.Dependency;
import nut.model.Project;
import nut.artifact.Artifact;

import java.io.File;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

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

    public void execute( Project project ) throws GoalException
    {
        log = new Log();
        Properties pp               = project.getProperties();
        String basedir              = (String)pp.getProperty( "basedir" );
        String sourceDirectory      = project.getLayout().getSourceDirectory();
        String testSourceDirectory  = project.getLayout().getTestSourceDirectory();
        String outputDirectory      = project.getLayout().getOutputDirectory();
        String testOutputDirectory  = project.getLayout().getTestOutputDirectory();

        log.debug( "build.sourceDirectory     = " + sourceDirectory );
        log.debug( "build.testSourceDirectory = " + testSourceDirectory );
        log.debug( "build.outputDirectory     = " + outputDirectory );
        log.debug( "build.testOutputDirectory = " + testOutputDirectory );

        // List of dependencies file names
        List<String> dependencies = new ArrayList<String>();
        List<String> testDependencies = new ArrayList<String>();
        List deps = project.getDependencies();
        for ( int i = 0; i < deps.size(); i++ ) {
            Dependency dep = (Dependency)(deps.get(i));
            Artifact artifactDep = new Artifact( dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getType() );
            if( dep.getScope().equals("test") ) {
                testDependencies.add(artifactDep.getPath());
            } else {
              if( dep.getScope().equals("compile") ) {
                  dependencies.add(artifactDep.getPath());
              }
            }
        }

        /* Compiling sources */
        File outputDir = new File( basedir + File.separator + outputDirectory );
        if ( !outputDir.exists() ) {
            outputDir.mkdirs();
        }

        //List sources = sourceFiles( new File( basedir + File.separator + sourceDirectory ) );
        List sources = sourceFiles( new File( basedir + File.separator + sourceDirectory ) );
        if ( sources.isEmpty() ) {
            log.warn( "No source code for " + project.getId() );
        } else {
            log.info( "Compiling " + sourceDirectory );
            compile( sources, dependencies, basedir + File.separator + sourceDirectory, basedir + File.separator + outputDirectory );
        }

        /* Compiling test sources */
        File testOutputDir = new File( basedir + File.separator + testOutputDirectory );
        if ( !testOutputDir.exists() ) {
            testOutputDir.mkdirs();
        }

        List testSources = sourceFiles( new File( basedir + File.separator + testSourceDirectory ) );
        //testSources.addAll(sources);
        if ( !testSources.isEmpty() ) {
            if ( !testDependencies.isEmpty() )
                dependencies.addAll(testDependencies);
            dependencies.add( basedir + File.separator + outputDirectory );
            log.info( "Compiling " + testSourceDirectory );
            compile( testSources, dependencies, basedir + File.separator + testSourceDirectory, basedir + File.separator + testOutputDirectory );
        }

    }

    private List<String> sourceFiles( File sourceDir )
    {
        List<String> sources = new LinkedList<String>();
        if( !sourceDir.exists() )
            return sources;

        String[] sourcesList = sourceDir.list();
        log.debug( "Source directory is " + sourceDir.getAbsolutePath() );

        for (int i=0; i<sourcesList.length; i++) {
            File child = new File(sourceDir, sourcesList[i]);
            if (child.isDirectory()) {
                sources.addAll( sourceFiles( child ) );
            } else {
                if ( child.getName().endsWith(".java") ) {
                     log.debug( "- " + child.getPath() );
                     sources.add( child.getAbsolutePath() );
                }
            }

        }
        return sources;
    }

    private void compile(List sources, List dependencies, String sourceDirectory, String outputDirectory)
        throws GoalException
    {
        int n = 8 + sources.size();
        log.debug("      from \'" + sourceDirectory + "\' to \'" + outputDirectory + "\'" );
        String[] args = new String[n];
        args[0] = "-d";
        args[1] = outputDirectory;
        args[2] = "-O";
        //    args[]( "-verbose" );
        args[3] = "-deprecation";
        args[4] = "-classpath";
        String classpathEntries = outputDirectory;
        for ( int i = 0; i < dependencies.size(); i++ ) {
            classpathEntries = classpathEntries + File.pathSeparator + (String)(dependencies.get(i));
        }
        args[5] = classpathEntries;
        args[6] = "-sourcepath";
        args[7] = sourceDirectory;
        // and the sources, at last
        for ( int i = 0; i < sources.size(); i++ ) {
            args[8+i] = (String)(sources.get(i));
        }

        // ----------------------------------------------------------------------
        for ( int i=0; i<n; i++ ) {
            log.debug( "  '"+args[i]+"'" );
        }

        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        //int run(InputStream in, OutputStream out, OutputStream err, String... arguments)
        int rc = javac.run(null, null, null, args);
        if ( rc != 0 ) {
             throw new GoalException("Compiler error " + String.valueOf(rc));
        }
    }

}
