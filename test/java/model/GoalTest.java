package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/*
To be added in tests
  public java.util.Properties getConfiguration()
  public void setConfiguration( java.util.Properties configuration )
  public String getConfigurationValue( String key )
  public void setConfigurationValue( String key, String value )
*/

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

    @Test
    public void testDefaultModelEncoding()
    {
      Goal goal = new Goal();
      assertEquals( goal.getModelEncoding(), "UTF-8" );
    }

    @Test
    public void testModelEncoding()
    {
      String me = "ASCII";
      Goal goal = new Goal();
      goal.setModelEncoding( me );
      assertEquals( goal.getModelEncoding(), me );
    }

    @Test
    public void testValidate() throws ValidationException
    {
      Goal goal = new Goal();
      goal.setName( "build" );
      goal.validate();
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidationExceptionNullName() throws ValidationException
    {
      Goal goal = new Goal();
      goal.validate();
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidationExceptionEmptyName() throws ValidationException
    {
      Goal goal = new Goal();
      goal.setName( "" );
      goal.validate();
    }

}
