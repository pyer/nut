package nut.build;

import nut.build.DuplicateProjectException;

import java.util.List;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SorterTest
{
    List projects = null;

    @Test
    public void testHashCodeNullSafe() throws CycleDetectedException, DuplicateProjectException
    {
        new Sorter(projects).hashCode();
    }

    @Test
    public void testEqualsNullSafe() throws CycleDetectedException, DuplicateProjectException
    {
        assertNotNull( new Sorter(projects) );
    }

    @Test
    public void testEqualsIdentity() throws CycleDetectedException, DuplicateProjectException
    {
        Sorter thing = new Sorter(projects);
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe() throws CycleDetectedException, DuplicateProjectException
    {
        assertNotNull( new Sorter(projects).toString() );
    }





/*   
    public List getSortedProjects()
    {
        return sortedProjects;
    }

    public boolean hasMultipleProjects()
    {
        return sortedProjects.size() > 1;
    }
*/
}
