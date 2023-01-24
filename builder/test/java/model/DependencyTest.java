package nut.model;

import nut.annotations.Test;

import static nut.Assert.assertEquals;
import static nut.Assert.assertNotNull;
import static nut.Assert.assertFalse;
import static nut.Assert.assertTrue;
import static nut.Assert.fail;

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
      assertEquals( dependency.getGroup(), "/g1/g2/g3" );
      assertEquals( dependency.getLibName(), "/dep-x-1.2.3.jar" );
    }

    @Test
    public void testDependencyEmptyName()
    {
      String s = "/g1/g2/g3/";
      Dependency dependency = new Dependency(s);
      assertEquals( dependency.getGroup(), "/g1/g2/g3" );
      assertEquals( dependency.getLibName(), "/" );
    }

    @Test
    public void testDependencyEmptyGroup()
    {
      String s = "/dep-x-1.2.3.jar";
      Dependency dependency = new Dependency(s);
      assertEquals( dependency.getGroup(), "" );
      assertEquals( dependency.getLibName(),  "/dep-x-1.2.3.jar" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateNullDependency() throws ValidationException
    {
      Dependency dependency = new Dependency(null);
      dependency.validate();
      fail( "Exception should be detected" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateEmptyDependency() throws ValidationException
    {
      Dependency dependency = new Dependency("");
      dependency.validate();
      fail( "Exception should be detected" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateWrongDependency1() throws ValidationException
    {
      Dependency dependency = new Dependency("abc");
      dependency.validate();
      fail( "Exception should be detected" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateWrongDependency2() throws ValidationException
    {
      Dependency dependency = new Dependency("/abc");
      dependency.validate();
      fail( "Exception should be detected" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateWrongDependency3() throws ValidationException
    {
      Dependency dependency = new Dependency("/abc/");
      dependency.validate();
      fail( "Exception should be detected" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateWrongDependency4() throws ValidationException
    {
      Dependency dependency = new Dependency("/a/b/c/");
      dependency.validate();
      fail( "Exception should be detected" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateWrongDependency5() throws ValidationException
    {
      Dependency dependency = new Dependency("/a ");
      dependency.validate();
      fail( "Exception should be detected" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateWrongDependency6() throws ValidationException
    {
      Dependency dependency = new Dependency("\t/a");
      dependency.validate();
      fail( "Exception should be detected" );
    }

    @Test
    public void testDependencyIsPresent()
    {
      String basedir = System.getProperty("nut.basedir", ".");
      Dependency dependency = new Dependency("/nut/model/DependencyTest.class");
      assertFalse( dependency.isNotHere(basedir + "/target/test-classes") );
    }

    @Test
    public void testDependencyIsAbsent()
    {
      String basedir = System.getProperty("nut.basedir", ".");
      Dependency dependency = new Dependency("/nut/model/NoDependency");
      assertTrue( dependency.isNotHere(basedir = "/target/test-classes") );
    }

}
