package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class BuildTest
{
    @Test
    public void testDirectory()
    {
        String dir = "target";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getDirectory(), dir );
        // check set value
        thing.setDirectory( dir );
        assertEquals( thing.getDirectory(), dir );
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
        String dir = "src/java";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getSourceDirectory(), dir );
        // check set value
        thing.setOutputDirectory( dir );
        assertEquals( thing.getSourceDirectory(), dir );
    }

    @Test
    public void testTestSourceDirectory()
    {
        String dir = "test/java";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getTestSourceDirectory(), dir );
        // check set value
        thing.setOutputDirectory( dir );
        assertEquals( thing.getTestSourceDirectory(), dir );
    }

    @Test
    public void testTestSuiteFile()
    {
        String dir = "test/testng.xml";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getTestSuiteFile(), dir );
        // check set value
        thing.setTestSuiteFile( dir );
        assertEquals( thing.getTestSuiteFile(), dir );
    }

    @Test
    public void testOutputDirectory()
    {
        String dir = "target/classes";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getOutputDirectory(), dir );
        // check set value
        thing.setOutputDirectory( dir );
        assertEquals( thing.getOutputDirectory(), dir );
    }

    @Test
    public void testTestOutputDirectory()
    {
        String dir = "target/test-classes";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getTestOutputDirectory(), dir );
        // check set value
        thing.setTestOutputDirectory( dir );
        assertEquals( thing.getTestOutputDirectory(), dir );
    }

    @Test
    public void testTestReportDirectory()
    {
        String dir = "target/test-reports";
        Build thing = new Build();
        // check default value
        assertEquals( thing.getTestReportDirectory(), dir );
        // check set value
        thing.setTestReportDirectory( dir );
        assertEquals( thing.getTestReportDirectory(), dir );
    }

    @Test
    public void testDefaultModelEncoding()
    {
        Build thing = new Build();
        assertEquals( thing.getModelEncoding(), "UTF-8" );
    }

    @Test
    public void testModelEncoding()
    {
        String me = "ASCII";
        Build thing = new Build();
        thing.setModelEncoding( me );
        assertEquals( thing.getModelEncoding(), me );
    }

    @Test
    public void testHashCodeNullSafe()
    {
        new Build().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertFalse( new Build().equals( null ) );
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

}
