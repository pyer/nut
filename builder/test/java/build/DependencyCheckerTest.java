package nut.build;

import nut.annotations.Test;

import static nut.Assert.assertEquals;
import static nut.Assert.assertNotNull;
import static nut.Assert.assertFalse;
import static nut.Assert.assertTrue;

public class DependencyCheckerTest
{

    @Test
    public void testHashCodeNullSafe()
    {
        new DependencyChecker().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertNotNull( new DependencyChecker() );
    }

    @Test
    public void testEqualsIdentity()
    {
        DependencyChecker thing = new DependencyChecker();
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new DependencyChecker().toString() );
    }

}
