package nut.build;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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
