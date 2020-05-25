package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

//import nut.layout.ProjectLayouter;
//import nut.layout.LayoutException;
import nut.model.Project;
import nut.model.ValidationException;

public class LayoutTest
{
    @Test
    public void testHashCodeNullSafe()
    {
        new Layout().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertNotNull( new Layout() );
    }

    @Test
    public void testEqualsIdentity()
    {
        Layout thing = new Layout();
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Layout().toString() );
    }

    @Test
    public void testBaseDirectory()
    {
        String dir = "base";
        Layout thing = new Layout();
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
        Layout thing = new Layout();
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
        Layout thing = new Layout();
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
        Layout thing = new Layout();
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
        Layout thing = new Layout();
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
        Layout thing = new Layout();
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
        Layout thing = new Layout();
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
        Layout thing = new Layout();
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
        Layout thing = new Layout();
        // check default value
        assertEquals( thing.getTestReportDirectory(), "target/test-reports" );
        // check set value
        thing.setTestReportDirectory( dir );
        assertEquals( thing.getTestReportDirectory(), dir );
    }

    @Test
    public void testTestSuite()
    {
        String s = "test-suite";
        Layout thing = new Layout();
        // check default value
        assertEquals( thing.getTestSuite(), "test/testng.xml" );
        // check set value
        thing.setTestSuite( s );
        assertEquals( thing.getTestSuite(), s );
    }

    @Test
    public void testValidate() throws ValidationException
    {
        Layout thing = new Layout();
        thing.validate();
    }

}
