package nut.build;

import nut.build.SorterException;
import nut.logging.Log;
import nut.model.Dependency;
import nut.model.Project;

import java.util.ArrayList;
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
        listOfProjects = projects;
    }

    public void checkDuplicate() throws SorterException
    {
        List<Project> projects = new ArrayList<Project>();
        log.debug("Check duplicate");
        for ( Project project : listOfProjects ) {
            log.debug("  " + project.getId() );
            // Check if a project is duplicate
            if ( projects.contains( project ) ) {
                throw new SorterException( "Project '" + project.getId() + "' is duplicated" );
            }
            projects.add( project );
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
        List<String> visited = new ArrayList<String>();
        log.debug("Check cyclic dependency");
        for ( Project project : listOfProjects ) {
            visit( project, visited);
        }
    }

    /**
     * Sort the projects regarding the dependencies.
     */
    public void sortProjects()
    {
        log.debug("Sort projects");
        for ( Project project : listOfProjects ) {
            sortBuildOrder( project, sortedProjects );
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
            Project dep = retrieveProject( dependency.getPath() );
            if ( dep != null && dep.isNotVisited() ) {
                visit( dep, cycle );
            }
            cycle.add( project.getPath() );
        }
        project.visited();
    }

    /**
     * This method will be called for each project to sort them for build them regarding the dependencies.
     */
    private void sortBuildOrder( Project project, List<Project> sorted )
    {
        log.debug("Sorting " + project.getPath() );
        for ( Dependency dependency : project.getDependencies() ) {
            Project dep = retrieveProject( dependency.getPath() );
            if ( dep != null ) {
                sortBuildOrder( dep, sorted );
            }
            if ( listOfProjects.contains( dep ) && ! sorted.contains( dep ) ) {
                sorted.add( dep );
            }
        }
        if ( ! sorted.contains( project ) ) {
                sorted.add( project );
        }
    }

    /**
     * Retrieve the project from the list of projects given its path
     */
    private Project retrieveProject( String path ) {
        for ( Project project : listOfProjects ) {
            if ( path.equals( project.getPath() ) ) {
              return project;
            }
        }
        return null;
    }

}
