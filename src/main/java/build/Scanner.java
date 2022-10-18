package nut.build;

import nut.logging.Log;
import nut.model.Project;
import nut.model.ParserException;
import nut.model.ValidationException;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


public class Scanner
{
  private Log log;
  private List<Project> projectsList;
  private String nut;

  /**
   * Scan a project with or without modules.
   */
  public Scanner( String nutFileName ) throws IOException, ParserException, ValidationException
  {
    // Default is noop mode is false
    this( nutFileName, false );
  }

  public Scanner( String nutFileName, boolean noop ) throws IOException, ParserException, ValidationException
  {
    log = new Log();
    nut = nutFileName;
    File projectFile = new File( nut );
    if ( projectFile.exists() ) {
      List<File> files = new ArrayList<File>();
      files.add( projectFile );
      projectsList = collectProjects( files, noop );
    } else {
      throw new ParserException( "Project file '" + nutFileName + "' not found !" );
    }
  }

  /**
   * return the project list of modules
   */
  public List<Project> getProjects()
  {
    return projectsList;
  }
  // --------------------------------------------------------------------------------
  private List<Project> collectProjects( List<File> files, boolean noop ) throws IOException, ParserException, ValidationException
  {
        List<Project> projects = new ArrayList<Project>( files.size() );

        for ( File file : files ) {
            File cfile = file.getCanonicalFile();
            log.debug("   Project " + cfile.getPath());
            Project project = new Project(noop);
            project.setBaseDirectory(cfile.getParent());
            project.parseFile( cfile );
            project.validate();
            if ( ( project.getModules() != null ) && !project.getModules().isEmpty() ) {
              //log.info("   Modules:");
                File modulesRoot = cfile.getParentFile();

                // Initial ordering is as declared in the modules section
                List<File> moduleFiles = new ArrayList<File>( project.getModules().size() );
                for ( String name : project.getModules() ) {
                    log.info("   - Module " + name);
                    if ( name.trim().isEmpty() ) {
                        log.warn( "Empty module detected. Please check you don't have any empty module definitions." );
                        continue;
                    }

                    File moduleFile = new File( modulesRoot, name );
                    if ( moduleFile.exists() && moduleFile.isDirectory() ) {
                        moduleFiles.add( new File( modulesRoot, name + File.separator + nut ) );
                    }
                }
                List<Project> collectedProjects = collectProjects( moduleFiles, noop );
                projects.addAll( collectedProjects );
            } else {
                projects.add( project );
            }
        }
        return projects;
  }
}
