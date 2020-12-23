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
      String s = "/nut/group/dep-3.0.jar";
      Dependency dependency = new Dependency(s);
      assertEquals( dependency.getPath(), s );
    }

    @Test
    public void testDependencyMavenPath()
    {
      assertEquals( new Dependency("/nut/group/dep-3.0.jar").getMavenPath(), "nut/group/dep/3.0/dep-3.0.jar" );
    }

    @Test
    public void testDependencyGroupPath()
    {
      assertEquals( new Dependency("/nut/group/dep-3.0.jar").getGroupPath(), "nut/group" );
    }

    @Test
    public void testDependencyIdRelease()
    {
      String s = "/nut/group/dep-3.0.jar";
      Dependency dependency = new Dependency(s);
      assertEquals( dependency.getId(), "nut.group:dep:3.0:jar" );
    }

    @Test
    public void testDependencyIdSnapshot()
    {
      String s = "/nut/group/dep-3.0-SNAPSHOT.jar";
      Dependency dependency = new Dependency(s);
      assertEquals( dependency.getId(), "nut.group:dep:3.0-SNAPSHOT:jar" );
    }

    @Test
    public void testEmptyDependencyId()
    {
      Dependency dependency = new Dependency("");
      assertEquals( dependency.getId(), ":::" );
    }

    @Test
    public void testBadDependencyId()
    {
      assertEquals( new Dependency("dep-3.0.jar").getId(),  ":dep:3.0:jar" );
      assertEquals( new Dependency("/dep-3.0.jar").getId(), ":dep:3.0:jar" );
      assertEquals( new Dependency("/nut/group.jar").getId(), "nut:group::jar" );
      assertEquals( new Dependency("/nut/group/dep.jar").getId(), "nut.group:dep::jar" );
      assertEquals( new Dependency("/nut/group/dep-3.0").getId(), "nut.group:dep:3:0" );
    }

    @Test
    public void testDependencyIsPresent()
    {
      String basedir = System.getProperty( "basedir", "." );
      Dependency dependency = new Dependency("nut/model/DependencyTest.class");
      assertFalse( dependency.isNotHere(basedir + "/target/test-classes") );
    }

    @Test
    public void testDependencyIsAbsent()
    {
      String basedir = System.getProperty( "basedir", "." );
      Dependency dependency = new Dependency("nut/model/NoDependency");
      assertTrue( dependency.isNotHere(basedir = "/target/test-classes") );
    }

}
