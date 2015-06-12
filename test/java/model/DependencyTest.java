package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;


public class DependencyTest
{

    @Test
    public void testHashCodeNullSafe()
    {
        new Dependency().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertFalse( new Dependency().equals( null ) );
    }

    @Test
    public void testEqualsIdentity()
    {
        Dependency thing = new Dependency();
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Dependency().toString() );
    }

}
