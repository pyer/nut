package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class ModelTest
{

    @Test
    public void testHashCodeNullSafe()
    {
        new Model().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertNotNull( new Model() );
    }

    @Test
    public void testEqualsIdentity()
    {
        Model thing = new Model();
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Model().toString() );
    }

    @Test
    public void testModelId()
    {
        Model model = new Model();
        model.setGroupId("group");
        model.setArtifactId("artifact");
        model.setVersion("1.0");
        assertEquals( model.getId(), "group:artifact:1.0" );
    }

    @Test
    public void testModelDefaultPackaging()
    {
        Model model = new Model();
        assertEquals( model.getPackaging(), "modules" );
    }

    @Test
    public void testModelPackaging()
    {
        Model model = new Model();
        model.setPackaging("zip");
        assertEquals( model.getPackaging(), "zip" );
    }


}
