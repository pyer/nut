package nut;

import nut.logging.Log;

import nut.artifact.Artifact;
import nut.execution.BuildFailureException;
import nut.execution.Execution;
import nut.execution.ExecutionException;

import nut.project.NutProject;
import nut.project.DuplicateProjectException;
import nut.project.ProjectBuilder;
import nut.project.ProjectBuildingException;
import nut.project.ProjectSorter;

import nut.model.EffectiveModel;

import java.io.File;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Nut
{
    /** Instance logger */
    private static Log log;

    private static final long MB = 1024 * 1024;

    private static final int MS_PER_SEC = 1000;

    private static final int SEC_PER_MIN = 60;

    private static final String POM_FILE = "nut.xml";

    public static void main( String[] args )
    {
        int exitCode = 0;
        List<String> goals = new ArrayList<String>();
        boolean effectiveNut = false;

        log = new Log();
        if( args.length>0 )
        {
	   for(int i=0; i < args.length ; i++)
           {
              if(args[i].equals("-h") || args[i].equals("--help") || args[i].equals("help") || args[i].equals("?") )
              {
                 showHelp();
                 System.exit( exitCode );
              }
              else if(args[i].equals("-v") || args[i].equals("--version") )
              {
                 showVersion();
                 System.exit( exitCode );
              }
              else if(args[i].startsWith("-D") )
              {
                 // -Dproperty=value (-Dproperty means -Dproperty=true)
                 setDefine(args[i]);
              }
              else if(args[i].equals("-X") || args[i].equals("--debug") )
              {
                 log.debugOn();
              }
              else if(args[i].equals("-e") || args[i].equals("--effective") )
              {
                 effectiveNut = true;
              }
              else
              {
                 if(args[i].startsWith("-") )
                 {
                    log.error( "Option [" + args[i] + "] is invalid.\n" );
                    showHelp();
                    System.exit( 101 );
                 }
                 // nearly every arg without '-' is a goal
                 goals.add(args[i]);
              }
           }
        }
        else
        {
                 showHelp();
                 System.exit( 102 );
        }
        if (goals.isEmpty() && effectiveNut==false ) {
                 showHelp();
                 System.exit( 102 );
        } 
        
        try
        {
            ScanningProject(goals, effectiveNut);
        }
        catch ( Exception e )
        {
//            e.printStackTrace();
            exitCode = 100;
        }
        finally
        {
            System.exit( exitCode );
        }
    }

    // ----------------------------------------------------------------------
    // Show functions
    // ----------------------------------------------------------------------
    private static void showVersion()
    {
        System.out.println( "Nut version       : " + System.getProperty( "nut.version", "<unknown>" ) );
        System.out.println( "Nut home          : " + System.getProperty( "nut.home", "<unknown>" ) );
        System.out.println( "Java version      : " + System.getProperty( "java.version", "<unknown>" ) );
        System.out.println( "Java home         : " + System.getProperty( "java.home", "<unknown>" ) );
        System.out.println( "Java classpath    : " + System.getProperty( "java.class.path", "<unknown>" ) );
        System.out.println( "Java vendor       : " + System.getProperty( "java.vendor", "<unknown>" ) );
        System.out.println( "Operating System  : " + System.getProperty( "os.name", "<unknown>" )
                                                   + System.getProperty( "os.version", "<unknown>" ) );
        System.out.println( "Architecture      : " + System.getProperty( "os.arch", "<unknown>" ) );
        System.out.println( "Default locale    : " + Locale.getDefault() );
        System.out.println( "Platform encoding : " + System.getProperty( "file.encoding", "<unknown encoding>" ) );
        System.out.println( "User name         : " + System.getProperty( "user.name", "<unknown encoding>" ) );
        System.out.println( "User home         : " + System.getProperty( "user.home", "<unknown encoding>" ) );
        System.out.println( "Working directory : " + System.getProperty( "user.dir", "<unknown encoding>" ) );
    }

    private static void showHelp()
    {
        System.out.println( "usage: nut [options] build" );
        System.out.println( "       nut [options] [goals]" );
        System.out.println( "\nGoals: clean compile test pack install deploy" );
        System.out.println( "\nOptions:" );
        System.out.println( " -D,--define      Define a system property" );
        System.out.println( " -X,--debug       Produce execution debug output" );
        System.out.println( " -e,--effective   Display effective NUT" );
        System.out.println( " -h,--help        Display this help" );
        System.out.println( " -v,--version     Display version information" );
    }

    // ----------------------------------------------------------------------
    // Arguments handling
    // ----------------------------------------------------------------------
    private static void setDefine( String define )
    {
        // ----------------------------------------------------------------------
        // Options that are set on the command line become system properties
        // and therefore are set in the session properties.
        // -Dproperty=value (-Dproperty means -Dproperty=true)
        // ----------------------------------------------------------------------
            String name;
            String value;
            int i = define.indexOf( "=" );
            if ( i <= 0 )
            {
                // no value means -Dname=true
                name = define.substring( 2, define.length() ).trim();
                value = "true";
            }
            else
            {
                name = define.substring( 2, i ).trim();
                value = define.substring( i + 1 ).trim();
            }
            name = "nut." + name;
            System.setProperty( name, value );
            log.debug("Define " + name + "=" + value );
    }

    // ----------------------------------------------------------------------
    // Project execution
    // ----------------------------------------------------------------------
    private static void ScanningProject( List goals, boolean effectiveNut )
        throws ExecutionException, BuildFailureException
    {
        ProjectSorter sorter;
        List sortedProjects;
        Date start   = new Date();
        try
        {
            line();
            log.info( "Started at " + start );
            log.info( "Scanning for projects..." );
            List files = Collections.EMPTY_LIST;
            File projectFile = new File( POM_FILE );
            if ( projectFile.exists() )
            {
                files = Collections.singletonList( projectFile );
            }
            else
            {
                throw new BuildFailureException(  "Project file '" + POM_FILE + "' not found !" );
            }

            ProjectBuilder builder = new ProjectBuilder(log);
            List<NutProject> projects = collectProjects( builder, files );
            if ( projects.isEmpty() )
            {
                throw new BuildFailureException(  "Project file '" + POM_FILE + "' is empty !" );
            }

            sorter = new ProjectSorter( projects );
            sortedProjects = sorter.getSortedProjects( );
            if ( sorter.hasMultipleProjects() )
            {
                line();
                log.info( "Ordering projects..." );
                for ( Iterator it = sortedProjects.iterator(); it.hasNext(); )
                {
                    NutProject currentProject = (NutProject) it.next();
                    log.info( "   " + currentProject.getId() );
                }
            }
            
            // iterate over projects, and execute on each...
            for ( Iterator it = sortedProjects.iterator(); it.hasNext(); )
            {
                NutProject currentProject = (NutProject) it.next();
                line();
                if( effectiveNut )
                {
                   EffectiveModel em = new EffectiveModel(currentProject.getModel());
                   log.info( em.getEffectiveModel() );
                }
                else
                {
                    long buildStartTime = System.currentTimeMillis();
                    Execution executor = new Execution( currentProject, log );
                    executor.executeGoals( goals );
                    currentProject.setStatus( System.currentTimeMillis() - buildStartTime, true );
                }
            }            
        }
            
        // --------------------------------------------------------------------------------
        catch ( BuildFailureException e )
        {
            logFailure( e );
            stats( start );
            throw new ExecutionException( e.getMessage(), e );
        }
/*        catch ( CycleDetectedException e )
        {
            logFailure( e );
            stats( start );
            throw new ExecutionException( "The projects in the reactor contain a cyclic reference: " + e.getMessage(), e );
        }
*/
        catch ( DuplicateProjectException e )
        {
            logFailure( e );
            stats( start );
            throw new ExecutionException( e.getMessage(), e );
        }
        catch ( ProjectBuildingException e )
        {
            logFailure( e );
            stats( start );
            throw new ExecutionException( e.getMessage(), e );
        }
        catch ( Throwable t )
        {
            logFatal( t );
            stats( start );
            throw new ExecutionException( "Error executing project within the reactor", t );
        }
        int failures = logReactorSummary( sorter );

        if ( failures>0 )
        {
/*            for ( Iterator it = sortedProjects.iterator(); it.hasNext(); )
            {
                NutProject project = (NutProject) it.next();
                if ( project.isBuilt() &&  !project.isSuccessful() )
                {
                    log.error( "Error for project: " + project.getId() + " (during " + project.getTask() + ")" );
                    logDiagnostics( project.getCause() );
                }
            }
*/
            log.info( "BUILD ERRORS" );
            stats( start );
            throw new ExecutionException( "Some builds failed" );
        }

        logSuccess( );
        stats( start );
    }
    // ----------------------------------------------------------------------

    private static List<NutProject> collectProjects( ProjectBuilder builder, List files )
        throws ProjectBuildingException, ExecutionException, BuildFailureException
//        throws ArtifactResolutionException, ProjectBuildingException, ExecutionException, BuildFailureException
    {
        List<NutProject> projects = new ArrayList<NutProject>( files.size() );

        for ( Iterator iterator = files.iterator(); iterator.hasNext(); )
        {
            File file = (File) iterator.next();
            log.debug("   Project " + file.getAbsolutePath());
            NutProject project = builder.build( file );

            if ( ( project.getModules() != null ) && !project.getModules().isEmpty() )
            {
            //log.info("   Modules:");
                File modulesRoot = file.getParentFile();

                // Initial ordering is as declared in the modules section
                List<File> moduleFiles = new ArrayList<File>( project.getModules().size() );
                for ( Iterator i = project.getModules().iterator(); i.hasNext(); )
                {
                    String name = (String) i.next();
                    log.info("   - Module " + name);
                    if ( name.trim().length()==0 )
                    {
                        log.warn( "Empty module detected. Please check you don't have any empty module definitions." );
                        continue;
                    }

                    File moduleFile = new File( modulesRoot, name );

                    if ( moduleFile.exists() && moduleFile.isDirectory() )
                    {
                        //moduleFile = new File( modulesRoot, name + "/" + Nut.POM_FILE );
                        moduleFiles.add( new File( modulesRoot, name + "/" + Nut.POM_FILE ) );
                    }

//                    moduleFile = new File( moduleFile.toURI().normalize() );
//                    moduleFiles.add( moduleFile );
                }
                List<NutProject> collectedProjects = collectProjects( builder, moduleFiles );
                projects.addAll( collectedProjects );
            }

            projects.add( project );
        }

        return projects;
    }

    // ----------------------------------------------------------------------
    // Logging
    // ----------------------------------------------------------------------

    protected static void logFatal( Throwable t )
    {
        log.error( "FATAL ERROR:" + t.getMessage() );
        t.printStackTrace();
        line();
    }

    protected static void logFailure( Exception e )
    {
        if( log.isDebug(true) ) {
           log.error( "BUILD FAILURE:" + e.getMessage() );
           e.printStackTrace();
        } else {
           log.error( "BUILD FAILURE" );
        }
        line();
    }

    protected static void logSuccess( )
    {
        log.info( "BUILD SUCCESSFUL" );
        line();
    }


    // ----------------------------------------------------------------------
    // Reporting
    // ----------------------------------------------------------------------
    // returns the number of failures
    private static int logReactorSummary( ProjectSorter sorter )
    {
        int failureCount = 0;
        if ( sorter.hasMultipleProjects() )
        {
            log.info( "" );
//            log.info( "" );

            // -------------------------
            // Reactor Summary:
            // -------------------------
            // o project-name...........FAILED
            // o project-name...........SUCCESS

            line();
            log.info( "Summary:" );
            line();

            for ( Iterator it = sorter.getSortedProjects().iterator(); it.hasNext(); )
            {
                NutProject project = (NutProject) it.next();

                if ( project.isBuilt() )
                {
                    if ( project.isSuccessful() )
                    {
                        logReactorSummaryLine( project.getId(), "SUCCESS", project.getTime() );
                    }
                    else
                    {
                        logReactorSummaryLine( project.getId(), "FAILED", project.getTime() );
                        failureCount++;
                    }
                }
                else
                {
                    logReactorSummaryLine( project.getId(), "NOT BUILT", -1 );
                }
            }
            line();
        }
        return failureCount;
    }

    private static void logReactorSummaryLine( String name, String status, long time )
    {
        StringBuffer messageBuffer = new StringBuffer();

        messageBuffer.append( name );

        int dotCount = 48;

        dotCount -= name.length();

        messageBuffer.append( " " );

        for ( int i = 0; i < dotCount; i++ )
        {
            messageBuffer.append( '.' );
        }

        messageBuffer.append( " " );

        messageBuffer.append( status );

        if ( time >= 0 )
        {
            messageBuffer.append( " [" );

            messageBuffer.append( getFormattedTime( time ) );

            messageBuffer.append( "]" );
        }

        log.info( messageBuffer.toString() );
    }

    private static String getFormattedTime( long time )
    {
        String pattern = "s.SSS's'";
        if ( time / 60000L > 0 )
        {
            pattern = "m:s" + pattern;
            if ( time / 3600000L > 0 )
            {
                pattern = "H:m" + pattern;
            }
        }
        DateFormat fmt = new SimpleDateFormat( pattern );
        fmt.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        return fmt.format( new Date( time ) );
    }

    private static void stats( Date start )
    {
        Date finish = new Date();
        long time = finish.getTime() - start.getTime();

//        line();
        log.info( "Total time: " + formatTime( time ) );
        log.info( "Finished at " + finish );
        line();
    }

    private static void line()
    {
        log.info( "------------------------------------------------------------------------" );
    }

    private static String formatTime( long ms )
    {
        long secs = ms / MS_PER_SEC;

        long min = secs / SEC_PER_MIN;

        secs = secs % SEC_PER_MIN;

        String msg = "";

        if ( min > 1 )
        {
            msg = min + " minutes ";
        }
        else if ( min == 1 )
        {
            msg = "1 minute ";
        }

        if ( secs > 1 )
        {
            msg += secs + " seconds";
        }
        else if ( secs == 1 )
        {
            msg += "1 second";
        }
        else if ( min == 0 )
        {
            msg += "< 1 second";
        }
        return msg;
    }

}
