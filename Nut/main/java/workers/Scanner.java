package nut.workers;

import nut.logging.Log;
import nut.project.Project;
import nut.project.ProjectBuilder;
import nut.project.BuildException;
import nut.workers.ScannerException;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class Scanner
{
  private final String POM_FILE = "nut.xml";
  private Log log;
  private List<Project> projects;

  /**
   * Scan a project with or without modules.
   * @throws ScannerException if any error
   */
  public Scanner( )
  {
    log = new Log();
    List files = Collections.EMPTY_LIST;
    File projectFile = new File( POM_FILE );
    log.info( "Scanning projects..." );
    if ( projectFile.exists() ) {
      files = Collections.singletonList( projectFile );
      ProjectBuilder builder = new ProjectBuilder();
      projects = collectProjects( builder, files );
      if ( projects.isEmpty() ) {
          log.error( "Project file '" + POM_FILE + "' is empty !" );
      }
    } else {
      log.error( "Project file '" + POM_FILE + "' not found !" );
    }
  }

  /**
   * return the project list of modules
   */
  public List getProjects()
  {
    return projects;
  }
  // --------------------------------------------------------------------------------
  private List<Project> collectProjects( ProjectBuilder builder, List files )
  {
        List<Project> projects = new ArrayList<Project>( files.size() );

        for ( Iterator iterator = files.iterator(); iterator.hasNext(); )
        {
            File file = (File) iterator.next();
            log.debug("   Project " + file.getAbsolutePath());
            try {
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
                        moduleFiles.add( new File( modulesRoot, name + "/" + POM_FILE ) );
                    }
                }
                List<Project> collectedProjects = collectProjects( builder, moduleFiles );
                projects.addAll( collectedProjects );
              }
            projects.add( project );
            } catch ( BuildException e ) {
              log.error( e.getMessage() );
              break;
            }
        }
        return projects;
  }
}
