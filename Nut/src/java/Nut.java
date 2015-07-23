package nut;

import nut.logging.Log;

import nut.artifact.Artifact;

import nut.project.Project;
import nut.project.BuildFailureException;
import nut.project.DuplicateProjectException;
import nut.project.ProjectBuilder;
import nut.project.ProjectSorter;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class Nut
{
    /** Instance logger */
    private static Log log;

    private static final String POM_FILE = "nut.xml";

    public static void main( String[] args )
    {
        String  goalArg      = null;
        String  effectiveNut = null; // xml or json
        boolean noopMode     = false;

        // Default mode is SNAPSHOT
        System.setProperty( "nut.mode", "SNAPSHOT" );

        log = new Log();
        if( args.length>0 ) {
           for(int i=0; i < args.length ; i++) {
              if(args[i].equals("-h") || args[i].equals("--help") || args[i].equals("help") || args[i].equals("?") ) {
                 showHelp();
                 System.exit( 0 );
              }
              else if(args[i].equals("-v") || args[i].equals("--version") ) {
                 showVersion();
                 System.exit( 0 );
              }
              else if(args[i].startsWith("-D") ) {
                 // -Dproperty=value (-Dproperty means -Dproperty=true)
                 setDefine(args[i]);
              }
              else if( args[i].equals("-d") || args[i].equals("--debug") ) {
                 log.debugOn();
              }
              else if( args[i].equals("-x") || args[i].equals("--xml") ) {
                 effectiveNut = "xml";
              }
              else if( args[i].equals("-j") || args[i].equals("--json") ) {
                 effectiveNut = "json";
              }
              else if( args[i].equals("-n") || args[i].equals("--noop") ) {
                 noopMode = true;
              }
              else if( args[i].equals("-r") || args[i].equals("--release") ) {
                 System.setProperty( "nut.mode", "RELEASE" );
              }
              else if( args[i].equals("-s") || args[i].equals("--snapshot") ) {
                 System.setProperty( "nut.mode", "SNAPSHOT" );
              }
              else {
                 if( args[i].startsWith("-") ) {
                    log.error( "Invalid option '" + args[i] + "'." );
                    showHelp();
                    System.exit( 1 );
                 }
                 // nearly every arg without '-' is a goal
                 if( goalArg==null ) {
                    goalArg = args[i];
                 } else {
                    log.error( "Too many goals." );
                    showHelp();
                    System.exit( 2 );
                 }
              }
           }
        } else {
                 showHelp();
                 System.exit( 3 );
        }
        if (effectiveNut==null ) {
            // every goal is 4 characters or more length
            if( goalArg==null || goalArg.length()<4 ) {
                 showHelp();
                 System.exit( 4 );
            }
        } 
        // everything is ok, let's go 
        log.start();
        List modules = scanningProject();
        if( modules != null ) {
          buildProject(modules, goalArg, effectiveNut, noopMode);
          if( modules.size() > 1 )
            logReactorSummary( modules );
        }
        log.finish();
        System.exit( 0 );
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
        log.out( "" );
        log.out( "Usage: nut [options] build" );
        log.out( "       nut [options] [goal]" );
        log.out( "\n  where [goal] is one of: clean compile test pack install deploy" );
        log.out( "\nOptions:" );
        log.out( " -h,--help        Display this help" );
        log.out( " -v,--version     Display version information" );
        log.out( " -D,--define      Define a system property" );
        log.out( " -d,--debug       Produce execution debug output" );
        log.out( " -x,--xml         Display effective NUT in xml format" );
        log.out( " -j,--json        Display effective NUT in json format" );
        log.out( " -n,--noop        No operation mode (dry run)" );
        log.out( " -r,--release     Release mode. Default is snapshot" );
        log.out( " -s,--snapshot    Snapshot default mode" );
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

    // ----------------------------------------------------------------------
    // Project execution
    // ----------------------------------------------------------------------
    private static List scanningProject()
    {
        List sortedProjects = Collections.EMPTY_LIST;
        try
        {
            log.info( "Scanning for projects..." );
            List files = Collections.EMPTY_LIST;
            File projectFile = new File( POM_FILE );
            if ( projectFile.exists() ) {
                files = Collections.singletonList( projectFile );
            } else {
                throw new BuildFailureException(  "Project file '" + POM_FILE + "' not found !" );
            }

            ProjectBuilder builder = new ProjectBuilder();
            List<Project> projects = collectProjects( builder, files );
            if ( projects.isEmpty() ) {
                throw new BuildFailureException(  "Project file '" + POM_FILE + "' is empty !" );
            }

            ProjectSorter sorter = new ProjectSorter( projects );
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
        }
        catch ( BuildFailureException e ) {
            log.failure( e );
        }
        catch ( DuplicateProjectException e ) {
            log.failure( e );
        }
        catch ( Throwable t ) {
            log.fatal( t );
        }
        return sortedProjects;
    }

    // --------------------------------------------------------------------------------
    private static void buildProject( List sortedProjects, String goalArgument, String effectiveNut, boolean noopMode )
    {
            // iterate over projects, and execute on each...
            for ( Iterator it = sortedProjects.iterator(); it.hasNext(); )
            {
                Project currentProject = (Project) it.next();
                log.line();
                if( effectiveNut == null ) {
                    currentProject.interpolateModel();
                    currentProject.checkDependencies();
                    currentProject.build( goalArgument, noopMode );
                } else if ( "xml".equals(effectiveNut) ) {
                    currentProject.effectiveXmlModel();
                } else if ( "json".equals(effectiveNut) ) {
                    currentProject.effectiveJsonModel();
                }
            }
    }

    // ----------------------------------------------------------------------

    private static List<Project> collectProjects( ProjectBuilder builder, List files )
        throws BuildFailureException
    {
        List<Project> projects = new ArrayList<Project>( files.size() );

        for ( Iterator iterator = files.iterator(); iterator.hasNext(); )
        {
            File file = (File) iterator.next();
            log.debug("   Project " + file.getAbsolutePath());
            Project project = builder.build( file );

            if ( ( project.getModules() != null ) && !project.getModules().isEmpty() ) {
            //log.info("   Modules:");
                File modulesRoot = file.getParentFile();

                // Initial ordering is as declared in the modules section
                List<File> moduleFiles = new ArrayList<File>( project.getModules().size() );
                for ( Iterator i = project.getModules().iterator(); i.hasNext(); )
                {
                    String name = (String) i.next();
                    log.info("   - Module " + name);
                    if ( name.trim().isEmpty() ) {
                        log.warn( "Empty module detected. Please check you don't have any empty module definitions." );
                        continue;
                    }

                    File moduleFile = new File( modulesRoot, name );
                    if ( moduleFile.exists() && moduleFile.isDirectory() ) {
                        moduleFiles.add( new File( modulesRoot, name + "/" + Nut.POM_FILE ) );
                    }
                }
                List<Project> collectedProjects = collectProjects( builder, moduleFiles );
                projects.addAll( collectedProjects );
            }

            projects.add( project );
        }

        return projects;
    }

    // ----------------------------------------------------------------------
    // Reporting
    // ----------------------------------------------------------------------
    private static void logReactorSummary( List modules )
    {
            // -------------------------
            // Reactor Summary:
            // -------------------------
            // o project-name...........FAILED
            // o project-name...........SUCCESS

            log.line();
            log.info( "SUMMARY" );

            for ( Iterator it = modules.iterator(); it.hasNext(); )
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
