package nut.build;

import nut.logging.Log;
import nut.model.Dependency;
import nut.model.Project;

import nut.build.CycleDetectedException;
import nut.build.CycleDetector;
import nut.build.DuplicateProjectException;
import nut.build.Vertex;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Sort projects by dependencies.
 *
 */
public class Sorter implements Cloneable, Serializable
{
    private Log log;
    private final List<Project> sortedProjects = new ArrayList<Project>();

    private final Map<String, Project> projectMap = new HashMap<String,Project>();
//    private final Map<Vertex, Integer> vertexStateMap = new HashMap<Vertex, Integer>();
    private final Map<String, Vertex> vertexMap = new HashMap<>();
    private final List<Vertex> vertexList = new LinkedList<Vertex>();

    private List<String> sortedList = new ArrayList<String>();
    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------

    /**
     * Sort a list of projects.
     * <ul>
     * <li>collect all the vertices for the projects that we want to build.</li>
     * <li>iterate through the deps of each project and if that dep is within
     * the set of projects we want to build then add an edge, otherwise throw
     * the edge away because that dependency is not within the set of projects
     * we are trying to build. we assume a closed set.</li>
     * <li>do a topo sort on the graph that remains.</li>
     * </ul>
     * @throws CycleDetectedException in case of cyclic dependencies
     * @throws DuplicateProjectException if any projects are duplicated by id
     */
    public Sorter( List<Project> projects ) throws CycleDetectedException, DuplicateProjectException
    {
        log = new Log();
        log.debug("Sorting the list of projects");
        if ( projects == null || projects.isEmpty() ) {
          log.debug("Empty list");
          return;
        }

        // Create projectMap
        for ( Project project : projects ) {
            String id = project.getId();
            if ( vertexMap.get( id ) != null ) {
                throw new DuplicateProjectException( "Project '" + id + "' is duplicated" );
            }
            addVertex( id );
            projectMap.put( id, project );
        }

        // Add dependencies
        for ( Project project : projects ) {
            String id = project.getId();
            for ( Dependency dependency : project.getDependencies() ) {
                String dep = dependency.getId();
                if ( vertexMap.get( dep ) != null ) {
                    addEdge( id, dep );
                }
            }
        }

        // Convert vertex list to sorted projects list
        sortedList = dfs( vertexList );
        for ( String name : sortedList ) {
            sortedProjects.add( projectMap.get( name ) );
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

    private List<String> dfs( List<Vertex> vertexList )
    {
        final List<String> retValue = new LinkedList<String>();
        final Map<Vertex, Integer> vertexStateMap = new HashMap<>();

        for ( Vertex vertex : vertexList ) {
            if ( CycleDetector.isNotVisited( vertex, vertexStateMap ) ) {
                dfsVisit( vertex, vertexStateMap, retValue );
            }
        }
        return retValue;
    }

    private void dfsVisit( final Vertex vertex, final Map<Vertex, Integer> vertexStateMap, final List<String> list )
    {
        vertexStateMap.put( vertex, CycleDetector.visiting() );
        for ( Vertex v : vertex.getChildren() ) {
            if ( CycleDetector.isNotVisited( v, vertexStateMap ) ) {
                dfsVisit( v, vertexStateMap, list );
            }
        }

        vertexStateMap.put( vertex, CycleDetector.visited() );
        list.add( vertex.getLabel() );
    }

    // ------------------------------------------------------------

    /**
     * Adds vertex to DAG. If vertex of given label already exist in DAG no vertex is added
     * DAG = Directed Acyclic Graph
     *
     * @param label The label of the Vertex
     * @return New vertex if vertex of given label was not present in the DAG or existing vertex if vertex of given
     *         label was already added to DAG
     */
    private Vertex addVertex( final String label )
    {
        Vertex retValue = null;

        log.debug("  addVertex " + label);
        // check if vertex is already in DAG
        if ( vertexMap.containsKey( label ) ) {
            retValue = vertexMap.get( label );
        } else {
            retValue = new Vertex( label );
            vertexMap.put( label, retValue );
            vertexList.add( retValue );
        }
        return retValue;
    }

    private void addEdge( final String current, final String child ) throws CycleDetectedException
    {
        final Vertex from = addVertex( current );
        final Vertex to = addVertex( child );

        log.debug("  addEdge from " + current + " to " + child);
        from.addEdgeTo( to );
//        to.addEdgeFrom( from );
        final List<String> cycle = CycleDetector.introducesCycle( to );
        if ( cycle != null ) {
            // remove edge which introduced cycle
            from.removeEdgeTo( to );
//            to.removeEdgeFrom( from );
            final String msg = "Edge between '" + from + "' and '" + to + "' introduces to cycle in the graph";
            throw new CycleDetectedException( msg, cycle );
        }
    }

}
