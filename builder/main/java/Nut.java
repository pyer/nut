package nut;

import nut.Logger;

import nut.build.Builder;
import nut.build.Scanner;
import nut.build.Sorter;
import nut.build.SorterException;
import nut.model.ParserException;
import nut.model.Project;
import nut.model.ValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Nut
{
    private static int retCode = 0;
    private static Logger log;
    private static String[] buildSteps = {"clean", "compile", "test", "pack", "install"};
    private static List<String> runArguments = new ArrayList<String>();

    public static void main( String[] args )
    {
        String  goal = null;
        boolean noop = false;
        // Default version mode is SNAPSHOT
        String  mode = "-SNAPSHOT";

        log = new Logger();
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
            } else if (args[i].equals("env") || args[i].equals("model") || args[i].equals("build") || args[i].equals("run")) {
                goal = args[i];
            } else if (args[i].startsWith("-D")) {
                // -Dproperty=value (-Dproperty means -Dproperty=true)
                setDefine(args[i]);
            } else if (args[i].equals("-d") || args[i].equals("--debug")) {
                log.setLevel( Logger.Level.DEBUG );
            } else if (args[i].equals("-v") || args[i].equals("--verbose")) {
                log.setLevel( Logger.Level.TRACE );
            } else if (args[i].equals("-n") || args[i].equals("--noop")) {
                noop = true;
            } else if (args[i].equals("-r") || args[i].equals("--release")) {
                mode = "";
            } else if (args[i].equals("-s") || args[i].equals("--snapshot")) {
                // nothing to do, default mode is already snapshot
                ;
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

        if (goal.equals("env")) {
            showEnvironment();
            System.exit( 0 );
        }

        if (goal.equals("model")) {
            Scanner scanner = new Scanner("nut.yaml");
            try {
              Project project = scanner.getProject();
              log.out( project.model() );
            } catch(Exception e) {
              log.out( e.getMessage() );
            }
            System.exit( 0 );
        }

        // Everything is ok, let's go
        log.start();
        try {
            Scanner scanner = new Scanner("nut.yaml", noop);
            List<Project> projects = scanner.getProjects();
            Sorter sorter = new Sorter( projects );
            sorter.checkDuplicate();
            sorter.checkCyclicDependency();
            sorter.sortProjects();
            List<Project> sortedProjects = sorter.getSortedProjects();
            // iterate over projects, and execute goal on each...
            for ( Project project : sortedProjects ) {
                project.setArguments(runArguments);
                project.setVersionMode(mode);
                log.line();
                Builder builder = new Builder(goal);
                retCode += builder.build(project);
            }
            if( sortedProjects.size() > 1 ) {
               logReactorSummary( sortedProjects );
            }
        } catch(SorterException e) {
            log.failure(e);
            retCode = 5;
        } catch(ValidationException e) {
            log.failure(e);
            retCode = 6;
        } catch(ParserException e) {
            log.failure(e);
            retCode = 6;
        } catch(Exception e) {
            log.failure(e);
            retCode = 7;
        }
        log.finish();
        System.exit( retCode );
    }

    // ----------------------------------------------------------------------
    // Help functions
    // ----------------------------------------------------------------------
    private static void showHelp()
    {
        log.out( "Version " + System.getProperty( "nut.version" ) );
        log.out( "\nUsage:" );
        log.out( "    nut <operation> [options]" );
        log.out( "\nOperations:" );
        log.out( "  env      Display project's environment" );
        log.out( "  model    Display project's model from nut.yaml file" );
        log.out( "  build    Build project, execute 'clean', 'compile', 'test', 'pack' and 'install' operations" );
        log.out( "  clean    Clean up built files in target directory" );
        log.out( "  compile  Compile source files" );
        log.out( "  test     Test compiled sources" );
        log.out( "  pack     Pack binaries and resources " );
        log.out( "  install  Install package in local repository");
        log.out( "  run      Run project" );
        log.out( "\nOptions:" );
        log.out( "  -D,--define      Define a system property (-Dkey=value)" );
        log.out( "  -d,--debug       Display debug messages" );
        log.out( "  -n,--noop        No operation mode (dry run)" );
        log.out( "  -r,--release     Install a release version (default is snapshot)" );
        log.out( "  -s,--snapshot    Install a snapshot version of the project" );
        log.out( "  -v,--verbose     Display more messages" );
        log.out( "  --               Next arguments are passed to the operation call (mainly run)" );
        log.out( "\n" );
    }

    private static void showEnvironment()
    {
        log.out( "Nut version           : " + System.getProperty( "nut.version", "<undefined>" ) );
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
                    if ( project.failed() ) {
                        log.failure( project.getPath(), project.getTime() );
                    } else {
                        log.success( project.getPath(), project.getTime() );
                    }
                } else {
                    log.warning( project.getPath(), -1 );
                }
            }
    }
}
