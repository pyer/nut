package nut.build;

import nut.build.SorterException;
import nut.logging.Log;
import nut.model.Dependency;
import nut.model.Project;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Sort projects by dependencies.
 *
 */
public class Sorter
{
    private Log log;
    private List<Project> listOfProjects = new ArrayList<Project>();
    private List<Project> sortedProjects = new ArrayList<Project>();

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------

    public Sorter( List<Project> projects )
    {
        log = new Log();
//        log.debugOn();
        listOfProjects = projects;
    }

    public void checkDuplicate() throws SorterException
    {
        List<Project> linkedProjects = new LinkedList<Project>();
        log.debug("Check duplicate");
        for ( Project project : listOfProjects ) {
            log.debug("  " + project.getId() );
            // Check if a project is duplicate
            if ( linkedProjects.contains( project ) ) {
                throw new SorterException( "Project '" + project.getId() + "' is duplicated" );
            }
            linkedProjects.add( project );
        }
    }

    /**
     * iterate through the deps of each project and if that dep is within
     * the set of projects we want to build then add an edge, otherwise throw
     * the edge away because that dependency is not within the set of projects
     * we are trying to build. we assume a closed set.
     * @throws SorterException if any projects are duplicated by id and in case of cyclic dependencies
     */
    public void checkCyclicDependency() throws SorterException
    {
        List<String> visited = new LinkedList<String>();
        log.debug("Check cyclic dependency");
        for ( Project project : listOfProjects ) {
            visit( project, visited);
        }
    }

    // TO DO
    public void sortProjects()
    {
        log.debug("Sort projects");
        // Create sorted list of projects
        for ( Project project : listOfProjects ) {
                sortedProjects.add( project );
        }

    }

    public List<Project> getSortedProjects()
    {
        return sortedProjects;
    }

    public boolean hasMultipleProjects()
    {
        return sortedProjects.size() > 1;
    }

    // ------------------------------------------------------------
    /**
     * This method will be called for each project and we want to check if introduction of
     * this project introduces a cycle in the graph.
     */
    private void visit( Project project, List<String> cycle ) throws SorterException
    {
        log.debug("Visiting " + project.getPath() );
        project.visiting();
        for ( Dependency dependency : project.getDependencies() ) {
            if ( cycle.contains( dependency.getPath() ) ) {
                     throw new SorterException( "Project '" + project.getPath() + "' creates a cycle with '" + dependency.getPath() + "'" );
            }
            log.debug("  dependency " + dependency.getPath() );
            Project dep = findProject( dependency.getPath() );
            if ( dep != null && dep.isNotVisited() ) {
                visit( dep, cycle );
            }
            cycle.add( project.getPath() );
        }            
        project.visited();
    }


    private Project findProject( String path ) {
        for ( Project project : listOfProjects ) {
            if ( path.equals( project.getPath() ) ) {
              return project;
            }
        }
        return null;
    }

}
