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
    public void testToString()
    {
      Goal goal = new Goal();
      goal.setName( "test" );
      assertEquals( "Test", goal.toString() );
      goal.setType( "id" );
      assertEquals( "TestId", goal.toString() );
    }

    @Test
    public void testGetClassName()
    {
      Goal goal = new Goal();
      assertEquals( "Test", goal.getClassName("test") );
    }

    @Test
    public void testConfiguration()
    {
      Goal goal = new Goal();
      assertTrue( goal.configuration().isEmpty() );
      assertFalse( goal.hasConfiguration() );
      goal.setConfigurationValue( "key", "value" );
      assertEquals( "value", goal.getConfigurationValue( "key" ) );
      assertTrue( goal.hasConfiguration() );
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
