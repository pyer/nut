package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Tests {@code Repository}.
 * 
 */
public class RepositoryTest
{

    @Test
    public void testHashCodeNullSafe()
    {
        new Repository().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertFalse( new Repository().equals( null ) );
    }

    @Test
    public void testEqualsIdentity()
    {
        Repository repository = new Repository();
        assertTrue( repository.equals( repository ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Repository().toString() );
    }

    @Test
    public void testName()
    {
      String s = "test_repository";
      Repository repository = new Repository();
      repository.setName( s );
      assertEquals( repository.getName(), s );
    }

    @Test
    public void testLayout()
    {
      String s = "maven";
      Repository repository = new Repository();
      // Default layout is 'nut'
      assertEquals( repository.getLayout(), "nut" );
      repository.setLayout( s );
      assertEquals( repository.getLayout(), s );
    }

    @Test
    public void testURL()
    {
      String s = "test_url";
      Repository repository = new Repository();
      // Default url is null
      assertNull( repository.getURL() );
      repository.setURL( s );
      assertEquals( repository.getURL(), s );
    }

}
