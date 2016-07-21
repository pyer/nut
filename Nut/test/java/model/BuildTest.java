package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import nut.model.ValidationException;

import nut.project.Project;
import nut.project.ProjectBuilder;
import nut.project.BuildException;

public class BuildTest
{
    @Test
    public void testHashCodeNullSafe()
    {
        new Build().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertNotNull( new Build() );
    }

    @Test
    public void testEqualsIdentity()
    {
        Build thing = new Build();
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Build().toString() );
    }

    @Test
    public void testBaseDirectory()
    {
        String dir = "base";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getBaseDirectory(), "." );
        // check set value
        thing.setBaseDirectory( dir );
        assertEquals( thing.getBaseDirectory(), dir );
    }

    @Test
    public void testSourceDirectory()
    {
        String dir = "java";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getSourceDirectory(), "main/java" );
        // check set value
        thing.setSourceDirectory( dir );
        assertEquals( thing.getSourceDirectory(), dir );
    }

    @Test
    public void testResourceDirectory()
    {
        String dir = "resources";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getResourceDirectory(), "main/resources" );
        // check set value
        thing.setResourceDirectory( dir );
        assertEquals( thing.getResourceDirectory(), dir );
    }

    @Test
    public void testTestSourceDirectory()
    {
        String dir = "java";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getTestSourceDirectory(), "test/java" );
        // check set value
        thing.setTestSourceDirectory( dir );
        assertEquals( thing.getTestSourceDirectory(), dir );
    }

    @Test
    public void testTestResourceDirectory()
    {
        String dir = "resources";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getTestResourceDirectory(), "test/resources" );
        // check set value
        thing.setTestResourceDirectory( dir );
        assertEquals( thing.getTestResourceDirectory(), dir );
    }

    @Test
    public void testTargetDirectory()
    {
        String dir = "tmp";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getTargetDirectory(), "target" );
        // check set value
        thing.setTargetDirectory( dir );
        assertEquals( thing.getTargetDirectory(), dir );
    }

    @Test
    public void testOutputDirectory()
    {
        String dir = "classes";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getOutputDirectory(), "target/classes" );
        // check set value
        thing.setOutputDirectory( dir );
        assertEquals( thing.getOutputDirectory(), dir );
    }

    @Test
    public void testTestOutputDirectory()
    {
        String dir = "test-classes";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getTestOutputDirectory(), "target/test-classes" );
        // check set value
        thing.setTestOutputDirectory( dir );
        assertEquals( thing.getTestOutputDirectory(), dir );
    }

    @Test
    public void testTestReportDirectory()
    {
        String dir = "test-reports";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getTestReportDirectory(), "target/test-reports" );
        // check set value
        thing.setTestReportDirectory( dir );
        assertEquals( thing.getTestReportDirectory(), dir );
    }

    @Test
    public void testGetGoals()
    {
        Build thing = new Build();
        assertNotNull( thing.getGoals() );
        assertTrue(  thing.getGoals().isEmpty() );
    }

    @Test
    public void testSetGoals()
    {
        Build thing = new Build();
        Goal goal = new Goal();
        ArrayList<Goal> goals = new ArrayList<Goal>(Arrays.asList(goal));
        thing.setGoals( goals );
        assertFalse( thing.getGoals().isEmpty() );
    }

    @Test
    public void testValidate() throws ValidationException
    {
        Build thing = new Build();
        thing.validate();
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidationException() throws ValidationException
    {
        try {
          ProjectBuilder builder = new ProjectBuilder();
          Project project = builder.build( new File("test/resources/badGoal.xml") );
          project.getBuild().validate();
        }
        catch ( BuildException e ) {
            throw new ValidationException( "Build failure" );
        }
    }

}
