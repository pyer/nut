package nut.build;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ScannerTest
{
    String nut = "src/test/resources/fullProject.yml";

    @Test
    public void testHashCodeNullSafe()
    {
        new Scanner(nut).hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertNotNull( new Scanner(nut) );
    }

    @Test
    public void testEqualsIdentity()
    {
        Scanner thing = new Scanner(nut);
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Scanner(nut).toString() );
    }


}
