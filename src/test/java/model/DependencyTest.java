package nut.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;


public class DependencyTest
{

    @Test
    public void testHashCodeNullSafe()
    {
        new Dependency("").hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertNotNull( new Dependency("") );
    }

    @Test
    public void testEqualsIdentity()
    {
        Dependency thing = new Dependency("");
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Dependency("").toString() );
    }

    @Test
    public void testDependencyPath()
    {
      String s = "/g1/g2/g3/dep-x-1.2.3.jar";
      Dependency dependency = new Dependency(s);
      assertEquals( dependency.getPath(), s );
    }

    @Test
    public void testDependencyGroup()
    {
      String s = "/g1/g2/g3/dep-x-1.2.3.jar";
      Dependency dependency = new Dependency(s);
      assertEquals( dependency.getGroup(), "/g1/g2/g3" );
    }

    @Test
    public void testDependencyEmptyGroup()
    {
      String s = "/dep-x-1.2.3.jar";
      Dependency dependency = new Dependency(s);
      assertEquals( dependency.getGroup(), "" );
    }

/*
    @Test
    public void testBadDependencyPath()
    {
      assertEquals( new Dependency("dep-3.0.jar").getPath(),        "/dep-3.0.jar" );
      assertEquals( new Dependency("/dep-3.0.jar").getPath(),       "/dep:3.0.jar" );
      assertEquals( new Dependency("/nut/group.jar").getPath(),     "/nut/group-.jar" );
      assertEquals( new Dependency("/nut/group/dep.jar").getPath(), "/nut/group/dep-.jar" );
      assertEquals( new Dependency("/nut/group/dep-3.0").getPath(), "/nut/group/dep-3.0" );
    }
*/

    @Test
    public void testDependencyIsPresent()
    {
      String basedir = System.getProperty( "basedir", "." );
      Dependency dependency = new Dependency("/nut/model/DependencyTest.class");
      assertFalse( dependency.isNotHere(basedir + "/target/test-classes") );
    }

    @Test
    public void testDependencyIsAbsent()
    {
      String basedir = System.getProperty( "basedir", "." );
      Dependency dependency = new Dependency("/nut/model/NoDependency");
      assertTrue( dependency.isNotHere(basedir = "/target/test-classes") );
    }

}
