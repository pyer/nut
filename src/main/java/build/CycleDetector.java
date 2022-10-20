package nut.build;

import nut.build.Vertex;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CycleDetector
{

    private final static Integer NOT_VISITED = 0;
    private final static Integer VISITING = 1;
    private final static Integer VISITED = 2;

    /**
     * This method will be called when an edge leading to given vertex was added and we want to check if introduction of
     * this edge has not resulted in apparition of cycle in the graph
     *
     * @param vertex the vertex
     * @param vertexStateMap the vertex Map
     * @return the found cycle
     */
    public static List<String> introducesCycle( final Vertex vertex, final Map<Vertex, Integer> vertexStateMap )
    {
        final LinkedList<String> cycleStack = new LinkedList<>();

        final boolean hasCycle = dfsVisit( vertex, cycleStack, vertexStateMap );

        if ( hasCycle )
        {
            // we have a situation like: [b, a, c, d, b, f, g, h].
            // Label of Vertex which introduced the cycle is at the first position in the list
            // We have to find second occurrence of this label and use its position in the list
            // for getting the sublist of vertex labels of cycle participants
            //
            // So in our case we are searching for [b, a, c, d, b]
            final String label = cycleStack.getFirst();
            final int pos = cycleStack.lastIndexOf( label );
            final List<String> cycle = cycleStack.subList( 0, pos + 1 );
            Collections.reverse( cycle );
            return cycle;
        }
        return null;
    }

    public static List<String> introducesCycle( final Vertex vertex )
    {
        final Map<Vertex, Integer> vertexStateMap = new HashMap<>();
        return introducesCycle( vertex, vertexStateMap );
    }

    public static Integer visiting()
    {
        return VISITING;
    }

    public static Integer visited()
    {
        return VISITED;
    }

    public static boolean isNotVisited( final Vertex vertex, final Map<Vertex, Integer> vertexStateMap )
    {
        final Integer state = vertexStateMap.get( vertex );
        return ( state == null ) || NOT_VISITED.equals( state );
    }

    private static boolean isVisiting( final Vertex vertex, final Map<Vertex, Integer> vertexStateMap )
    {
        final Integer state = vertexStateMap.get( vertex );
        return VISITING.equals( state );
    }

    private static boolean dfsVisit( final Vertex vertex, final LinkedList<String> cycle,
                                     final Map<Vertex, Integer> vertexStateMap )
    {
        cycle.addFirst( vertex.getLabel() );
        vertexStateMap.put( vertex, VISITING );
        for ( Vertex v : vertex.getChildren() ) {
            if ( isNotVisited( v, vertexStateMap ) ) {
                final boolean hasCycle = dfsVisit( v, cycle, vertexStateMap );
                if ( hasCycle ) {
                    return true;
                }
            } else if ( isVisiting( v, vertexStateMap ) ) {
                cycle.addFirst( v.getLabel() );
                return true;
            }
        }
        vertexStateMap.put( vertex, VISITED );
        cycle.removeFirst();
        return false;
    }

}
