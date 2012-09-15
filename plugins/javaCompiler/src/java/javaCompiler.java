package nut.plugins;

/*
Javac.exe is used by this plugin
--------------------------------

Usage: javac <options> <source files>
where possible options include:
  -g                         Generate all debugging info
  -g:none                    Generate no debugging info
  -g:{lines,vars,source}     Generate only some debugging info
  -nowarn                    Generate no warnings
  -verbose                   Output messages about what the compiler is doing
  -deprecation               Output source locations where deprecated APIs are used
  -classpath <path>          Specify where to find user class files and annotation processors
  -cp <path>                 Specify where to find user class files and annotation processors
  -sourcepath <path>         Specify where to find input source files
  -bootclasspath <path>      Override location of bootstrap class files
  -extdirs <dirs>            Override location of installed extensions
  -endorseddirs <dirs>       Override location of endorsed standards path
  -proc:{none,only}          Control whether annotation processing and/or compilation is done.
  -processor <class1>[,<class2>,<class3>...]Names of the annotation processors to run; bypasses default discovery process
  -processorpath <path>      Specify where to find annotation processors
  -d <directory>             Specify where to place generated class files
  -s <directory>             Specify where to place generated source files
  -implicit:{none,class}     Specify whether or not to generate class files for implicitly referenced files 
  -encoding <encoding>       Specify character encoding used by source files
  -source <release>          Provide source compatibility with specified release
  -target <release>          Generate class files for specific VM version
  -version                   Version information
  -help                      Print a synopsis of standard options
  -Akey[=value]              Options to pass to annotation processors
  -X                         Print a synopsis of nonstandard options
  -J<flag>                   Pass <flag> directly to the runtime system

*/

import nut.logging.Log;

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
public class javaCompiler
{
    /** Instance logger */
    private static Log log;

    public static void execute(Properties pluginContext, List<String> dependencies, List<String> testDependencies )
        throws Exception
    {
        log =new Log();
        String basedir = (String)pluginContext.getProperty( "basedir" );
        String buildDirectory      = (String)pluginContext.getProperty( "build.directory" );
        String sourceDirectory     = (String)pluginContext.getProperty( "build.sourceDirectory" );
        String testSourceDirectory = (String)pluginContext.getProperty( "build.testSourceDirectory" );
        String outputDirectory     = (String)pluginContext.getProperty( "build.outputDirectory" );
        String testOutputDirectory = (String)pluginContext.getProperty( "build.testOutputDirectory" );

        log.debug( "build.directory           = " + buildDirectory );
        log.debug( "build.sourceDirectory     = " + sourceDirectory );
        log.debug( "build.testSourceDirectory = " + testSourceDirectory );
        log.debug( "build.outputDirectory     = " + outputDirectory );
        log.debug( "build.testOutputDirectory = " + testOutputDirectory );
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
