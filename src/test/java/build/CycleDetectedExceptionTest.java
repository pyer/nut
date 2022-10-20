package nut.build;

import nut.build.CycleDetectedException;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
/*
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
*/

public class CycleDetectedExceptionTest
{

    @Test
    public void testException()
    {
        final List<String> cycle = new ArrayList<String>();
        cycle.add( "a" );
        cycle.add( "b" );
        cycle.add( "a" );
        final CycleDetectedException e = new CycleDetectedException( "Cycle detected", cycle );
        assertEquals( e.getMessage(), "Cycle detected --> a --> b --> a" );
    }

    @Test
    public void testGetCycle()
    {
        final List<String> cycle = new ArrayList<String>();
        cycle.add( "a" );
        cycle.add( "b" );
        final CycleDetectedException e = new CycleDetectedException( "Cycle detected", cycle );
        assertEquals( e.getCycle(), cycle );
    }

}
