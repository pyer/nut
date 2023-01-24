package nut.build;

import nut.Logger;
import nut.model.Project;
import nut.model.ParserException;
import nut.model.ValidationException;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


public class Scanner
{
    private Logger log;
    private List<Project> projectsList;
    private String nut;
    private boolean noop;

    /**
     * Scan a project with or without modules.
     */
    public Scanner( String nutFileName )
    {
      // Default is noop mode is false
      this( nutFileName, false );
    }

    /**
     * Scan a project with or without modules.
     */
    public Scanner( String nutFileName, boolean noOperation )
    {
      log = new Logger();
      nut  = nutFileName;
      noop = noOperation;
    }

    // --------------------------------------------------------------------------------
    /**
     * return the current project
     */
    public Project getProject() throws IOException, ParserException, ValidationException
    {
      Project project = new Project();
      File projectFile = new File( nut );
      if ( projectFile.exists() ) {
        project = createProject( projectFile );
      } else {
        throw new ParserException( "Project file '" + nut + "' not found !" );
      }
      return project;
    }

    /**
     * return the project list of modules
     */
    public List<Project> getProjects() throws IOException, ParserException, ValidationException
    {
      File projectFile = new File( nut );
      if ( projectFile.exists() ) {
        List<File> files = new ArrayList<File>();
        files.add( projectFile );
        projectsList = collectProjects( files );
      } else {
        throw new ParserException( "Project file '" + nut + "' not found !" );
      }
      return projectsList;
    }

    // --------------------------------------------------------------------------------
    private List<Project> collectProjects( List<File> files ) throws IOException, ParserException, ValidationException
    {
        List<Project> projects = new ArrayList<Project>( files.size() );

        for ( File file : files ) {
            Project project = createProject(file);
            if ( ( project.getModules() != null ) && !project.getModules().isEmpty() ) {
                // Initial ordering is as declared in the modules section
                List<File> moduleFiles = new ArrayList<File>( project.getModules().size() );
                for ( String name : project.getModules() ) {
                    log.info("  - Module " + name);
                    if ( name.trim().isEmpty() ) {
                        log.warn( "Empty module detected. Please check you don't have any empty module definitions." );
                        continue;
                    }

                    String modulesRoot = project.getBaseDirectory();
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

    private Project createProject( File file ) throws IOException, ParserException, ValidationException
    {
        File cfile = file.getCanonicalFile();
        log.debug("   Project " + cfile.getPath());
        Project project = new Project(noop);
        project.setBaseDirectory(cfile.getParent());
        project.parseFile( cfile );
        project.validate();
        return project;
    }
}
