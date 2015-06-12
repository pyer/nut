package nut;

import nut.logging.Log;

import nut.artifact.Artifact;

import nut.project.NutProject;
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
        boolean effectiveNut = false;
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
              else if( args[i].equals("-e") || args[i].equals("--effective") ) {
                 effectiveNut = true;
              }
              else if( args[i].equals("-n") || args[i].equals("--noop") ) {
                 noopMode = true;
              }
              else if( args[i].equals("-r") || args[i].equals("--release") ) {
                 System.setProperty( "nut.mode", "RELEASE" );
              }
              else {
                 if( args[i].startsWith("-") ) {
                    log.error( "Option [" + args[i] + "] is invalid.\n" );
                    showHelp();
                    System.exit( 1 );
                 }
                 // nearly every arg without '-' is a goal
                 if( goalArg==null ) {
                    goalArg = args[i];
                 } else {
                    log.error( "Too many goals.\n" );
                    showHelp();
                    System.exit( 2 );
                 }
              }
           }
        } else {
                 showHelp();
                 System.exit( 3 );
        }
        if (effectiveNut==false ) {
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
        log.out( "usage: nut [options] build" );
        log.out( "       nut [options] [goal]" );
        log.out( "\n  where [goal] is one of: clean compile test pack install deploy" );
        log.out( "\nOptions:" );
        log.out( " -h,--help        Display this help" );
        log.out( " -v,--version     Display version information" );
        log.out( " -D,--define      Define a system property" );
        log.out( " -d,--debug       Produce execution debug output" );
        log.out( " -e,--effective   Display effective NUT" );
        log.out( " -n,--noop        No operation mode (dry run)" );
        log.out( " -r,--release     Release mode. Default is snapshot" );
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
            List<NutProject> projects = collectProjects( builder, files );
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
                    NutProject currentProject = (NutProject) it.next();
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
    private static void buildProject( List sortedProjects, String goalArgument, boolean effectiveNut, boolean noopMode )
    {
            // iterate over projects, and execute on each...
            for ( Iterator it = sortedProjects.iterator(); it.hasNext(); )
            {
                NutProject currentProject = (NutProject) it.next();
                log.line();
                if( effectiveNut ) {
                    currentProject.effectiveModel();
                } else {
                  if( !"modules".equals(currentProject.getPackaging()) ) {
                    currentProject.checkDependencies();
                    currentProject.build( goalArgument, noopMode );
                  }
                }
            }
    }

    // ----------------------------------------------------------------------

    private static List<NutProject> collectProjects( ProjectBuilder builder, List files )
        throws BuildFailureException
    {
        List<NutProject> projects = new ArrayList<NutProject>( files.size() );

        for ( Iterator iterator = files.iterator(); iterator.hasNext(); )
        {
            File file = (File) iterator.next();
            log.debug("   Project " + file.getAbsolutePath());
            NutProject project = builder.build( file );

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
                List<NutProject> collectedProjects = collectProjects( builder, moduleFiles );
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
                NutProject project = (NutProject) it.next();

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
