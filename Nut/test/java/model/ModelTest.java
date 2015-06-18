package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class ModelTest
{

    @Test
    public void testHashCodeNullSafe()
    {
        new Model().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertFalse( new Model().equals( null ) );
    }

    @Test
    public void testEqualsIdentity()
    {
        Model thing = new Model();
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Model().toString() );
    }

}
