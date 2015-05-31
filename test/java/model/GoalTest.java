package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Tests {@code Goal}.
 * 
 */
public class GoalTest
{

    @Test
    public void testHashCodeNullSafe()
    {
        new Goal().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertFalse( new Goal().equals( null ) );
    }

    @Test
    public void testEqualsIdentity()
    {
        Goal goal = new Goal();
        assertTrue( goal.equals( goal ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Goal().toString() );
    }

/*
public class Goal implements java.io.Serializable
  public String getName()
  public String getType()
  public String getId()
  public String getId( String name )
  public java.util.Properties getConfiguration()
  public String getConfigurationValue( String key )
  public void setName( String name )
  public void setType( String type )
  public void setConfiguration( java.util.Properties configuration )
  public void setConfigurationValue( String key, String value )
  public void validate()
*/

    @Test
    public void testName()
    {
      String s = "test_goal";
      Goal goal = new Goal();
      goal.setName( s );
      assertTrue( goal.getName().equals( s ) );
    }

    @Test
    public void testType()
    {
      String s = "test_goal";
      Goal goal = new Goal();
      // Default type is null
      assertNull( goal.getType() );
      goal.setType( s );
      assertTrue( goal.getType().equals( s ) );
    }

    @Test
    public void testGetId()
    {
      Goal goal = new Goal();
      assertTrue( goal.getId("test").equals( "Test" ) );
      goal.setName( "test" );
      assertTrue( goal.getId().equals( "Test" ) );
      goal.setType( "id" );
      assertTrue( goal.getId().equals( "TestId" ) );
    }

}
