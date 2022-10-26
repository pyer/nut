package nut;

import nut.build.Builder;

import nut.build.Scanner;
import nut.build.Sorter;
import nut.build.SorterException;
import nut.logging.Log;
import nut.model.Project;

import nut.model.ParserException;
import nut.model.ValidationException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class Nut
{
    private static int retCode = 0;
    private static Log log;
    private static String[] buildSteps = {"clean", "compile", "test", "pack", "install"};
    private static List<String> runArguments = new ArrayList<String>();

    public static void main( String[] args )
    {
        String  goal = null;
        boolean noop = false;

        // Default mode is SNAPSHOT
        System.setProperty( "nut.mode", "SNAPSHOT" );

        log = new Log();
        for (int i=0; i < args.length; i++) {
            if (args[i].equals("--")) {
                // Next arguments are passed to the goal call
                i++;
                while (i < args.length) {
                  runArguments.add(args[i++]);
                }
            } else if (args[i].equals("-h") || args[i].equals("--help") || args[i].equals("help") || args[i].equals("?")) {
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
                noop = true;
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
            Scanner scanner = new Scanner("nut.yaml", noop);
            List<Project> projects = scanner.getProjects();
            Sorter sorter = new Sorter( projects );
            sorter.checkDuplicate();
            sorter.checkCyclicDependency();
            sorter.sortProjects();
            List<Project> sortedProjects = sorter.getSortedProjects();
            if ( sorter.hasMultipleProjects() ) {
              log.line();
              log.info( "Sorting projects..." );
              for ( Project project : sortedProjects ) {
                  log.info( "   " + project.getId() );
              }

              log.line();
              log.info( "Building projects..." );
            }
            // iterate over projects, and execute goal on each...
            for ( Project project : sortedProjects ) {
                project.setArguments(runArguments);
                log.line();
                Builder builder = new Builder(goal);
                retCode += builder.build(project);
            }
            if( sortedProjects.size() > 1 ) {
               logReactorSummary( sortedProjects );
            }
        } catch(SorterException e) {
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
        log.out( "Nut version           : " + System.getProperty( "nut.version", "<undefined>" ) );
        log.out( "Nut mode              : " + System.getProperty( "nut.mode", "<undefined>" ) );
        log.out( "Nut local repository  : " + System.getProperty( "nut.local", "<undefined>" ) );
        log.out( "Nut remote repository : " + System.getProperty( "nut.remote", "<undefined>" ) );
        log.out( "Java version          : " + System.getProperty( "java.version", "<unknown>" ) );
        log.out( "Java home             : " + System.getProperty( "java.home", "<unknown>" ) );
        log.out( "Java vendor           : " + System.getProperty( "java.vendor", "<unknown>" ) );
        log.out( "Java classpath        : " + System.getProperty( "java.class.path", "" ) );
        log.out( "Java ext dirs         : " + System.getProperty( "java.ext.dirs", "" ) );
        log.out( "Operating System      : " + System.getProperty( "os.name", "<unknown>" )
                                            + System.getProperty( "os.version", "<unknown>" ) );
        log.out( "Architecture          : " + System.getProperty( "os.arch", "<unknown>" ) );
        log.out( "Platform encoding     : " + System.getProperty( "file.encoding", "<unknown encoding>" ) );
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
        log.out( "  --               Next arguments are passed to the operation call (mainly run)" );
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
    private static void logReactorSummary( List<Project> projects )
    {
            // -------------------------
            // Reactor Summary:
            // -------------------------
            // o project-name...........FAILED
            // o project-name...........SUCCESS
            log.line();
            log.info( "SUMMARY" );

            for ( Project project : projects ) {
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
