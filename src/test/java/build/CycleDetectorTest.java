package nut.build;

import nut.build.CycleDetectedException;
import nut.build.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class CycleDetectorTest
{

    private final Map<String, Vertex> vertexMap = new HashMap<>();

    private Vertex addVertex( final String label )
    {
        Vertex retValue = null;
        if ( vertexMap.containsKey( label ) ) {
            retValue = vertexMap.get( label );
        } else {
            retValue = new Vertex( label );
            vertexMap.put( label, retValue );
        }
        return retValue;
    }

    private void addEdge( final String current, final String child ) throws CycleDetectedException
    {
        final Vertex from = addVertex( current );
        final Vertex to = addVertex( child );

        from.addEdgeTo( to );
        final List<String> cycle = CycleDetector.introducesCycle( to );
        if ( cycle != null ) {
            from.removeEdgeTo( to );
            final String msg = "Edge between '" + from + "' and '" + to + "' introduces to cycle in the graph";
            throw new CycleDetectedException( msg, cycle );
        }
    }

    private boolean hasEdge( final String label1, final String label2 )
    {
        final Vertex v1 = vertexMap.get( label1 );
        final Vertex v2 = vertexMap.get( label2 );
        final boolean retValue = v1.getChildren().contains( v2 );
        return retValue;
    }

    @Test
    public void testNoCycleIsDetected()
    {
        // No cycle
        // a --> b --->c
        vertexMap.clear();
        try {
            addEdge( "a", "b" );
            addEdge( "b", "c" );
        } catch ( CycleDetectedException e ) {
            fail( "Cycle should not be detected" );
        }
    }

    @Test
    public void testCycle1()
    {
        vertexMap.clear();
        //
        // a --> b --->c
        // ^ |
        // | |
        // -----------|
        try {
            addEdge( "a", "b" );
            addEdge( "b", "c" );
            addEdge( "c", "a" );
            fail( "Cycle should be detected" );
        } catch ( CycleDetectedException e ) {
            final List<String> cycle = e.getCycle();
            assertNotNull( cycle, "Cycle should be not null" );
            assertTrue( cycle.contains( "a" ), "Cycle contains 'a'" );
            assertTrue( cycle.contains( "b" ), "Cycle contains 'b'" );
            assertTrue( cycle.contains( "c" ), "Cycle contains 'c'" );
        }
    }

    @Test
    public void testCycle2()
    {
        vertexMap.clear();
        // | --> c
        // a --> b
        // | | --> d
        // --------->
        try {
            addEdge( "a", "b" );
            addEdge( "b", "c" );
            addEdge( "b", "d" );
            addEdge( "a", "d" );
        } catch ( CycleDetectedException e ) {
            fail( "Cycle should not be detected" );
        }
    }

    @Test
    public void testCycle3()
    {
        vertexMap.clear();
        // ------------
        // | |
        // V | --> c
        // a --> b
        // | | --> d
        // --------->
        try {
            addEdge( "a", "b" );
            addEdge( "b", "c" );
            addEdge( "b", "d" );
            addEdge( "a", "d" );
            addEdge( "c", "a" );
            fail( "Cycle should be detected" );
        } catch ( CycleDetectedException e ) {
            final List<String> cycle = e.getCycle();
            assertNotNull( cycle, "Cycle should be not null" );
            assertEquals( cycle.get( 0 ), "a", "Cycle contains 'a'" );
            assertEquals( cycle.get( 1 ), "b", "Cycle contains 'b'" );
            assertEquals( cycle.get( 2 ), "c", "Cycle contains 'c'" );
            assertEquals( cycle.get( 3 ), "a", "Cycle contains 'a'" );
        }
    }

    @Test
    public void testCycle4()
    {
        vertexMap.clear();
        // f --> g --> h
        // |
        // |
        // a --> b ---> c --> d
        // ^ |
        // | V
        // ------------ e
        try {
            addEdge( "a", "b" );
            addEdge( "b", "c" );
            addEdge( "b", "f" );
            addEdge( "f", "g" );
            addEdge( "g", "h" );
            addEdge( "c", "d" );
            addEdge( "d", "e" );
            addEdge( "e", "b" );
            fail( "Cycle should be detected" );
        } catch ( CycleDetectedException e ) {
            final List<String> cycle = e.getCycle();
            assertNotNull( cycle, "Cycle should be not null" );
            assertEquals( cycle.size(), 5, "Cycle contains 5 elements" );
            assertEquals( cycle.get( 0 ), "b", "Cycle contains 'b'" );
            assertEquals( cycle.get( 1 ), "c", "Cycle contains 'c'" );
            assertEquals( cycle.get( 2 ), "d", "Cycle contains 'd'" );
            assertEquals( cycle.get( 3 ), "e", "Cycle contains 'e'" );
            assertEquals( cycle.get( 4 ), "b", "Cycle contains 'b'" );
            assertTrue( hasEdge( "a", "b" ), "Edge exists" );
            assertTrue( hasEdge( "b", "c" ), "Edge exists" );
            assertTrue( hasEdge( "b", "f" ), "Edge exists" );
            assertTrue( hasEdge( "f", "g" ), "Edge exists" );
            assertTrue( hasEdge( "g", "h" ), "Edge exists" );
            assertTrue( hasEdge( "c", "d" ), "Edge exists" );
            assertTrue( hasEdge( "d", "e" ), "Edge exists" );
            assertFalse( hasEdge( "e", "b" ) );
        }
    }

}
