package nut;

import nut.build.Builder;
/*
import nut.build.DependencyChecker;

*/

import nut.build.DuplicateProjectException;
import nut.build.Scanner;
import nut.build.Sorter;
import nut.logging.Log;
import nut.model.Project;

import nut.model.ParserException;
import nut.model.ValidationException;

import org.codehaus.plexus.util.dag.CycleDetectedException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Nut
{
    private static int retCode = 0;
    private static Log log;
    private static String[] buildSteps = {"clean", "compile", "test", "pack", "install"};

    public static void main( String[] args )
    {
        String  goal = null;
        boolean noopMode   = false;

        // Default mode is SNAPSHOT
        System.setProperty( "nut.mode", "SNAPSHOT" );

        log = new Log();
        for (int i=0; i < args.length ; i++) {
            if (args[i].equals("-h") || args[i].equals("--help") || args[i].equals("help") || args[i].equals("?")) {
                showHelp();
                System.exit( 0 );
            } else if (args[i].equals("model") || args[i].equals("build") || args[i].equals("run") || args[i].equals("version")) {
                goal = args[i];
            } else if (args[i].startsWith("-D")) {
                // -Dproperty=value (-Dproperty means -Dproperty=true)
                setDefine(args[i]);
            } else if (args[i].equals("-d") || args[i].equals("--debug")) {
                log.debugOn();
            } else if (args[i].equals("-n") || args[i].equals("--noop")) {
                noopMode = true;
            } else if (args[i].equals("-r") || args[i].equals("--release")) {
                System.setProperty( "nut.mode", "RELEASE" );
            } else if (args[i].equals("-s") || args[i].equals("--snapshot")) {
                System.setProperty( "nut.mode", "SNAPSHOT" );
                 
            } else {
                if (args[i].startsWith("-")) {
                    log.error( "Invalid option '" + args[i] + "'" );
                    showHelp();
                    System.exit( 1 );
                }
                // First arg without '-': this is an operation
                if (goal == null) {
                    // Check if arg is in life cycle list of operation
                    for (final String step : buildSteps) {
                        if (args[i].equals(step)) {
                            goal = step;
                        }
                    }
                    if (goal==null) {
                        log.error( "Invalid operation '" + args[i] + "'" );
                        showHelp();
                        System.exit( 2 );
                    }
                } else {
                    log.error( "Too many operations" );
                    showHelp();
                    System.exit( 3 );
                }
            }
        }

        // No operation is defined
        if (goal==null) {
            showHelp();
            System.exit( 0 );
        }

        if (goal.equals("version")) {
            showVersion();
            System.exit( 0 );
        }

        // Everything is ok, let's go
        log.start();
        try {
            log.info( "Scanning projects..." );
            Scanner scanner = new Scanner("nut.yaml");
            List projects = scanner.getProjects();
            Sorter sorter = new Sorter( projects );
            List sortedProjects = sorter.getSortedProjects();
            if ( sorter.hasMultipleProjects() ) {
              log.line();
              log.info( "Ordering projects..." );
              for ( Iterator it = sortedProjects.iterator(); it.hasNext(); )
              {
                  Project currentProject = (Project) it.next();
                  log.info( "   " + currentProject.getId() );
              }

              log.line();
              log.info( "Building projects..." );
            }
            // iterate over projects, and execute goal on each...
            for ( Iterator it = sortedProjects.iterator(); it.hasNext(); ) {
                Project project = (Project) it.next();
                log.line();
                Builder builder = new Builder(goal);
                retCode += builder.build(project, noopMode);
            }
            if( sortedProjects.size() > 1 ) {
               logReactorSummary( sortedProjects );
            }
        } catch(CycleDetectedException e) {
            log.failure(e.getMessage());
            retCode = 5;
        } catch(DuplicateProjectException e) {
            log.failure(e.getMessage());
            retCode = 5;
        } catch(ValidationException e) {
            log.failure(e.getMessage());
            retCode = 6;
        } catch(ParserException e) {
            log.failure(e.getMessage());
            retCode = 6;
        } catch(Exception e) {
            log.failure(e.getMessage());
            retCode = 7;
        }
        log.finish();
        System.exit( retCode );
    }

    // ----------------------------------------------------------------------
    // Help functions
    // ----------------------------------------------------------------------
    private static void showVersion()
    {
        log.out( "Nut version       : " + System.getProperty( "nut.version", "<unknown>" ) );
        log.out( "Nut home          : " + System.getProperty( "nut.home", "<unknown>" ) );
        log.out( "Nut mode          : " + System.getProperty( "nut.mode", "<unknown>" ) );
        log.out( "Java version      : " + System.getProperty( "java.version", "<unknown>" ) );
        log.out( "Java home         : " + System.getProperty( "java.home", "<unknown>" ) );
        log.out( "Java classpath    : " + System.getProperty( "java.class.path", "<unknown>" ) );
        log.out( "Java vendor       : " + System.getProperty( "java.vendor", "<unknown>" ) );
        log.out( "Operating System  : " + System.getProperty( "os.name", "<unknown>" )
                                        + System.getProperty( "os.version", "<unknown>" ) );
        log.out( "Architecture      : " + System.getProperty( "os.arch", "<unknown>" ) );
        log.out( "Default locale    : " + Locale.getDefault() );
        log.out( "Platform encoding : " + System.getProperty( "file.encoding", "<unknown encoding>" ) );
        log.out( "User name         : " + System.getProperty( "user.name", "<unknown encoding>" ) );
        log.out( "User home         : " + System.getProperty( "user.home", "<unknown encoding>" ) );
        log.out( "Working directory : " + System.getProperty( "user.dir", "<unknown encoding>" ) );
    }

    private static void showHelp()
    {
        log.out( "\nUsage:" );
        log.out( "    nut <operation> [options]" );
        log.out( "\nOperations:" );
        log.out( "  clean    Clean up built files in target directory" );
        log.out( "  compile  Compile source files" );
        log.out( "  test     Test compiled sources" );
        log.out( "  pack     Pack binaries and resources " );
        log.out( "  install  Install package in local repository");
        log.out( "  build    Build project, execute 'clean', 'compile', 'test', 'pack' and 'install' operations" );
        log.out( "  model    Display effective nut.yaml" );
        log.out( "  run      Run project" );
        log.out( "  version  Display version information" );
        log.out( "  help     Display this help" );
        log.out( "\nOptions:" );
        log.out( "  -D,--define      Define a system property" );
        log.out( "  -d,--debug       Display debug messages" );
        log.out( "  -h,--help        Display this help" );
        log.out( "  -n,--noop        No operation mode (dry run)" );
        log.out( "  -r,--release     Release mode. Default is snapshot" );
        log.out( "  -s,--snapshot    Snapshot default mode" );
//        log.out( "  -v,--verbose     Display info messages" );
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
            if ( i <= 0 ) {
                // no value means -Dname=true
                name = define.substring( 2, define.length() ).trim();
                value = "true";
            } else {
                name = define.substring( 2, i ).trim();
                value = define.substring( i + 1 ).trim();
            }
            name = "nut." + name;
            System.setProperty( name, value );
            log.debug("Define " + name + "=" + value );
    }

    // --------------------------------------------------------------------------------
    // Reporting
    // ----------------------------------------------------------------------
    private static void logReactorSummary( List projects )
    {
            // -------------------------
            // Reactor Summary:
            // -------------------------
            // o project-name...........FAILED
            // o project-name...........SUCCESS
            log.line();
            log.info( "SUMMARY" );

            for ( Iterator it = projects.iterator(); it.hasNext(); )
            {
                Project project = (Project) it.next();
                if ( project.isBuilt() ) {
                    if ( project.isSuccessful() ) {
                        log.success( project.getId(), project.getTime() );
                    } else {
                        log.failure( project.getId(), project.getTime() );
                    }
                } else {
                    log.warning( project.getId(), -1 );
                }
            }
    }
}
