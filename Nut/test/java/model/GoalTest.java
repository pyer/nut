package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.util.Properties;

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
        assertNotNull( new Goal() );
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
    public void testClassName()
    {
      String s = "GoalClass";
      Goal goal = new Goal();
      goal.setName("goalName");
      assertFalse( goal.hasClassName() );
      // Default class name is name
      assertTrue( goal.getClassName().equals( "GoalName" ) );
      goal.setClassName( s );
      assertTrue( goal.getClassName().equals( s ) );
    }

    @Test
    public void testDefaultClassName()
    {
      Goal goal = new Goal();
      assertEquals( "Test", goal.getClassName("test") );
    }

    @Test
    public void testConfiguration()
    {
      Goal goal = new Goal();
      Properties configuration = new Properties();
      configuration.setProperty( "key", "value" );
      assertTrue( goal.getConfiguration().isEmpty() );
      assertFalse( goal.hasConfiguration() );
      goal.setConfiguration( configuration );
      assertTrue( goal.hasConfiguration() );
      assertEquals( "value", goal.getConfiguration().getProperty( "key" ) );
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
