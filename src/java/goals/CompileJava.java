package nut.goals;

import nut.logging.Log;
import nut.project.NutProject;
import nut.model.Dependency;
import nut.artifact.Artifact;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import java.lang.InterruptedException;
import java.lang.Process;
import java.lang.Runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Compiles application sources
 *
 */
public class CompileJava
{
    /** Instance logger */
    private static Log log;

    public static void execute( NutProject project, Properties config )
        throws Exception
    {
        log = new Log();
        Properties pp               = project.getModel().getProperties();
        String basedir              = (String)pp.getProperty( "basedir" );
        String repository           = (String)pp.getProperty( "nut.home" );
        String sourceDirectory      = project.getBuild().getSourceDirectory();
        String testSourceDirectory  = project.getBuild().getTestSourceDirectory();
        String outputDirectory      = project.getBuild().getOutputDirectory();
        String testOutputDirectory  = project.getBuild().getTestOutputDirectory();

        log.debug( "build.sourceDirectory     = " + sourceDirectory );
        log.debug( "build.testSourceDirectory = " + testSourceDirectory );
        log.debug( "build.outputDirectory     = " + outputDirectory );
        log.debug( "build.testOutputDirectory = " + testOutputDirectory );

        // List of dependencies file names
        List<String> dependencies = new ArrayList<String>();
        List<String> testDependencies = new ArrayList<String>();
        List modelDep = project.getModel().getDependencies();
        for ( int i = 0; i < modelDep.size(); i++ )
        {
            Dependency dep = (Dependency)(modelDep.get(i));
            Artifact artifactDep = new Artifact( dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getType(), null );
            File file = artifactDep.getFile();
            //log.debug( "scope is " + dep.getScope() + " for " + dep.getId() );
            if( dep.getScope().equals("test") ) {
                testDependencies.add(file.getAbsolutePath());
            } else {
                dependencies.add(file.getAbsolutePath());
            }
        }
/*        
        if ( dependencies!=null )
        {
            for ( int i = 0; i < dependencies.size(); i++ )
            {
                log.debug( "project.dependency   = " + (String)(dependencies.get(i)) );
            }
        }
*/

        /* Compiling sources */
        File outputDir = new File( basedir + File.separator + outputDirectory );
        if ( !outputDir.exists() )
        {
            outputDir.mkdirs();
        }

        //List sources = sourceFiles( new File( basedir + File.separator + sourceDirectory ) );
        List sources = sourceFiles( new File( basedir + File.separator + sourceDirectory ) );
        if ( sources.isEmpty() )
        {
            log.warn( "Source directory " + sourceDirectory + " is empty" );
        }
        else
        {
            log.info( "   Compiling " + sourceDirectory );
            compile( sources, dependencies, basedir + File.separator + sourceDirectory, basedir + File.separator + outputDirectory );
        }
        
        /* Compiling test sources */
        File testOutputDir = new File( basedir + File.separator + testOutputDirectory );
        if ( !testOutputDir.exists() )
        {
            testOutputDir.mkdirs();
        }
        
        List testSources = sourceFiles( new File( basedir + File.separator + testSourceDirectory ) );
        //testSources.addAll(sources);
        if ( testSources.isEmpty() )
        {
            log.warn( "Test source directory " + testSourceDirectory + " is empty" );
        }
        else
        {
            if ( !testDependencies.isEmpty() )
                dependencies.addAll(testDependencies);
            dependencies.add( basedir + File.separator + outputDirectory );
            log.info( "   Compiling " + testSourceDirectory );
            compile( testSources, dependencies, basedir + File.separator + testSourceDirectory, basedir + File.separator + testOutputDirectory );
        }
        
    }

    private static List<String> sourceFiles( File sourceDir )
    {
        List<String> sources = new LinkedList<String>();
        if( !sourceDir.exists() )
            return sources;

        String[] sourcesList = sourceDir.list();
        log.debug( "Source directory is " + sourceDir.getAbsolutePath() );

        for (int i=0; i<sourcesList.length; i++)
        {
            File child = new File(sourceDir, sourcesList[i]);
            if (child.isDirectory())
            {
                sources.addAll( sourceFiles( child ) );
            }
            else
            {
                if ( child.getName().endsWith(".java") )
                {
                     log.debug( "- " + child.getPath() );
                     sources.add( child.getAbsolutePath() );
                }
            }

        }
        return sources;
    }

    private static void compile(List sources, List dependencies, String sourceDirectory, String outputDirectory)
        throws Exception
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
        for ( int i = 0; i < dependencies.size(); i++ )
        {
            classpathEntries = classpathEntries + File.pathSeparator + (String)(dependencies.get(i));
        }
        args[5] = classpathEntries;
        args[6] = "-sourcepath";
        args[7] = sourceDirectory;
        // and the sources, at last
        for ( int i = 0; i < sources.size(); i++ )
        {
            args[8+i] = (String)(sources.get(i));
        }

        // ----------------------------------------------------------------------
        for ( int i=0; i<n; i++ )
        {
            log.debug( "  '"+args[i]+"'" );
        }

        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        //int run(InputStream in, OutputStream out, OutputStream err, String... arguments)
        int rc = javac.run(null, null, null, args);
        if ( rc != 0 )
        {
             throw new Exception();
        }
    }

}
