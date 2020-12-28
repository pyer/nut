package nut.build;

import nut.logging.Log;
import nut.model.Project;
import nut.model.ParserException;
import nut.model.ValidationException;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    log = new Log();
    List files = Collections.EMPTY_LIST;
    nut = nutFileName;
    File projectFile = new File( nut );
    if ( projectFile.exists() ) {
      files = Collections.singletonList( projectFile );
      projectsList = collectProjects( files );
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
  private List<Project> collectProjects( List files ) throws IOException, ParserException, ValidationException
  {
        List<Project> projects = new ArrayList<Project>( files.size() );

        for ( Iterator iterator = files.iterator(); iterator.hasNext(); )
        {
            File file = ((File) iterator.next()).getCanonicalFile();
            log.debug("   Project " + file.getPath());
            Project project = new Project();
            project.setBaseDirectory(file.getParent());
            project.parseFile( file );
            project.validate();
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
                        moduleFiles.add( new File( modulesRoot, name + File.separator + nut ) );
                    }
                }
                List<Project> collectedProjects = collectProjects( moduleFiles );
                projects.addAll( collectedProjects );
            } else {
                projects.add( project );
            }
        }
        return projects;
  }
}
