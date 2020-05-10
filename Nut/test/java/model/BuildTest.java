package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import nut.build.ProjectBuilder;
import nut.build.BuildException;
import nut.model.ValidationException;
import nut.project.Project;

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
    public void testSuite()
    {
        String s = "clean";
        Build thing = new Build();
        thing.setSuite( s );
        assertEquals( thing.getSuite(), s );
    }

    @Test
    public void testValidate() throws ValidationException
    {
        Build thing = new Build();
        thing.validate();
    }
/*
    @Test
    public void testValidateGoodGoal() throws ValidationException
    {
        try {
          ProjectBuilder builder = new ProjectBuilder();
          Project project = builder.launch( new File("test/resources/goodGoal.xml") );
          project.getBuild().validate();
        }
        catch ( BuildException e ) {
            throw new ValidationException( "Build failure" );
        }
    }
*/
    @Test(expectedExceptions = ValidationException.class)
    public void testValidateBadGoal() throws ValidationException
    {
        try {
          ProjectBuilder builder = new ProjectBuilder();
          Project project = builder.launch( new File("test/resources/badGoal.xml") );
          project.getBuild().validate();
        }
        catch ( BuildException e ) {
            throw new ValidationException( "Build failure" );
        }
    }

}
