package nut;

import nut.build.DuplicateProjectException;
import nut.build.Scanner;
import nut.build.Sorter;

import nut.goals.Goal;
import nut.goals.GoalException;

import nut.goals.Clean;
import nut.goals.Compile;
import nut.goals.Install;
import nut.goals.PackJar;
import nut.goals.PackWar;
import nut.goals.PackZip;
import nut.goals.Test;

import nut.logging.Log;
import nut.model.Project;

import org.codehaus.plexus.util.dag.CycleDetectedException;

//import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Nut
{
    private static int retCode = 0;
    /** Instance logger */
    private static Log log;

    public static void main( String[] args )
    {
        String  wantedGoal = null;
        boolean noopMode   = false;

        // Default mode is SNAPSHOT
        System.setProperty( "nut.mode", "SNAPSHOT" );

        log = new Log();
        if( args.length>0 ) {
           for(int i=0; i < args.length ; i++) {
              if(args[i].equals("-h") || args[i].equals("--help") || args[i].equals("help") || args[i].equals("?") ) {
                 showHelp();
                 System.exit( 0 );
              } else if(args[i].startsWith("-D") ) {
                 // -Dproperty=value (-Dproperty means -Dproperty=true)
                 setDefine(args[i]);
              } else if( args[i].equals("-d") || args[i].equals("--debug") ) {
                 log.debugOn();
              } else if( args[i].equals("-n") || args[i].equals("--noop") ) {
                 noopMode = true;
              } else if( args[i].equals("-r") || args[i].equals("--release") ) {
                 System.setProperty( "nut.mode", "RELEASE" );
              } else if( args[i].equals("-s") || args[i].equals("--snapshot") ) {
                 System.setProperty( "nut.mode", "SNAPSHOT" );
              } else {
                 if( args[i].startsWith("-") ) {
                    log.error( "Invalid option '" + args[i] + "'." );
                    showHelp();
                    System.exit( 1 );
                 }
                 // nearly every arg without '-' is a goal
                 if( wantedGoal == null ) {
                    wantedGoal = args[i];
                 } else {
                    log.error( "Too many goals" );
                    showHelp();
                    System.exit( 2 );
                 }
              }
           }
        } else {
                 showHelp();
                 System.exit( 3 );
        }

        // every goal is 3 characters long or more
        if (wantedGoal==null || wantedGoal.length()<3 ) {
          log.error( "Unknown goal" );
          showHelp();
          System.exit( 4 );
        }
        if ("version".equals(wantedGoal)) {
          showVersion();
          System.exit( 0 );
        }

        // everything is ok, let's go
        log.start();
        List projects = Collections.EMPTY_LIST;
        Scanner scan = new Scanner();
        projects = scan.getProjects();

        List sortedProjects = Collections.EMPTY_LIST;
        if( projects != null ) {
          try {
            Sorter sorter = new Sorter( projects );
            sortedProjects = sorter.getSortedProjects();
            if ( sorter.hasMultipleProjects() ) {
              log.line();
              log.info( "Ordering projects..." );
              for ( Iterator it = sortedProjects.iterator(); it.hasNext(); )
              {
                  Project currentProject = (Project) it.next();
                  log.info( "   " + currentProject.getId() );
              }
            }
            buildProjects(sortedProjects, wantedGoal, noopMode);
          } catch(CycleDetectedException e) {
            log.failure(e.getMessage());
            retCode = 5;
          } catch(DuplicateProjectException e) {
            log.failure(e.getMessage());
            retCode = 6;
          } catch(Exception e) {
            log.failure(e.getMessage());
            retCode = 7;
          }
        }
        log.finish();
        System.exit( retCode );
    }

    // ----------------------------------------------------------------------
    // Show functions
    // ----------------------------------------------------------------------
    private static void showVersion()
    {
        log.out( "Nut version       : " + System.getProperty( "nut.version", "<unknown>" ) );
        log.out( "Nut home          : " + System.getProperty( "nut.home", "<unknown>" ) );
        log.out( "Nut mode          : " + System.getProperty( "nut.mode", "SNAPSHOT" ) );
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
        log.out( "    nut <goal> [options]" );
        log.out( "    nut build [options]" );
        log.out( "    nut list [options]" );
        log.out( "    nut xml  [options]" );
        log.out( "    nut json [options]" );
        log.out( "    nut version" );
        log.out( "    nut help" );
        log.out( "\nOperations:" );
        log.out( "  help     Display this help" );
        log.out( "  <goal>   Execute one of the project's build goals" );
        log.out( "  build    Build project, execute every goal" );
        log.out( "  list     List of build goals" );
        log.out( "  xml      Display effective NUT in xml format" );
        log.out( "  json     Display effective NUT in json format" );
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
    private static void buildProjects( List sortedProjects, String wantedGoal, boolean noopMode ) {
            // iterate over projects, and execute on each...
            for ( Iterator it = sortedProjects.iterator(); it.hasNext(); ) {
                Project project = (Project) it.next();
                log.line();
                if ( "list".equals(wantedGoal) ) {
                    log.info("Building " + project.getId() + " goals : " + project.getBuild());
                } else if ( "xml".equals(wantedGoal) ) {
                    log.info( "Effective XML model of " + project.getId() + "\n" + project.effectiveXmlNut());
                } else if ( "json".equals(wantedGoal) ) {
                    log.info( "Effective JSON model of " + project.getId() + "\n" + project.effectiveJsonNut());
                } else {
                    String[] suite = { wantedGoal };
                    if( "build".equals(wantedGoal) ) {
                        // if build is the wanted goal, every goal in the build suite is executed
                       suite = project.getBuild().split(" ");
                    }
//                    currentBuild.interpolateModel();
//                    currentBuild.checkDependencies();
                    retCode += buildProject(project, suite, noopMode);
                }
            }
            if( sortedProjects.size() > 1 ) {
               logReactorSummary( sortedProjects );
            }
    }

    // ----------------------------------------------------------------------
    /* returns 0 if success
     * returns 9 if not
     */
    private static int buildProject(Project project, String[] suite, boolean noopMode)
    {
      boolean fail = false;
      project.start();
      try {
        int len = suite.length;
        for (int i=0; i<len; i++) {
          String step=suite[i];
          if( noopMode ) {
            log.info( "NOOP: " + step + " " + project.getId() );
          } else {
              if( step.equals("clean") ) {
                new Clean().execute(project);
              } else if( step.equals("compile") ) {
                new Compile().execute(project);
              } else if( step.equals("test") ) {
                new Test().execute(project);
              } else if( step.equals("pack") ) {
                String type = project.getPackaging();
                if( type.equals("jar") ) {
                  new PackJar().execute(project);
                } else if( type.equals("war") ) {
                  new PackWar().execute(project);
                } else {
                  new PackZip().execute(project);
                }
              } else if( step.equals("install") ) {
                new Install().execute(project);
              } else {
                fail = true;
              }
          }
        }
      } catch ( GoalException e ) {
        log.debug(e.getMessage());
        fail = true;
      }

      if( fail ) {
        project.failure();
        log.failure( project.getId() );
        return 9;
      }
      project.success();
      return 0;
    }

    // ----------------------------------------------------------------------
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
            log.line();
    }
}
