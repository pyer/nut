package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;


public class DependencyTest
{

    @Test
    public void testHashCodeNullSafe()
    {
        new Dependency().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertFalse( new Dependency().equals( null ) );
    }

    @Test
    public void testEqualsIdentity()
    {
        Dependency thing = new Dependency();
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Dependency().toString() );
    }

    @Test
    public void testGroupId()
    {
      String s = "test_dependency";
      Dependency dependency = new Dependency();
      assertTrue( dependency.getGroupId() == null );
      dependency.setGroupId( s );
      assertTrue( dependency.getGroupId().equals( s ) );
    }

    @Test
    public void testArtifactId()
    {
      String s = "test_dependency";
      Dependency dependency = new Dependency();
      assertTrue( dependency.getArtifactId() == null );
      dependency.setArtifactId( s );
      assertTrue( dependency.getArtifactId().equals( s ) );
    }

    @Test
    public void testVersion()
    {
      String s = "1.0";
      Dependency dependency = new Dependency();
      // Default version is null
      assertNull( dependency.getVersion() );
      dependency.setVersion( s );
      assertTrue( dependency.getVersion().equals( s ) );
    }

    @Test
    public void testType()
    {
      String s = "zip";
      Dependency dependency = new Dependency();
      // Default type is jar
      assertTrue( dependency.getType().equals( "jar" ) );
      dependency.setType( s );
      assertTrue( dependency.getType().equals( s ) );
    }

    @Test
    public void testScope()
    {
      String s = "scope";
      Dependency dependency = new Dependency();
      // Default scope is compile
      assertTrue( dependency.getScope().equals( "compile" ) );
      dependency.setScope( s );
      assertTrue( dependency.getScope().equals( s ) );
    }

    @Test
    public void testGetId()
    {
      Dependency dependency = new Dependency();
      dependency.setGroupId( "group" );
      dependency.setArtifactId( "artifact" );
      dependency.setVersion( "1.0" );
      assertEquals( "group:artifact:1.0", dependency.getId() );
    }

    @Test
    public void testValidateWithVersion() throws ValidationException
    {
      Dependency dependency = new Dependency();
      dependency.setGroupId( "group" );
      dependency.setArtifactId( "artifact" );
      dependency.setVersion( "1.0" );
      dependency.validate( "" );
    }

    @Test
    public void testValidateWithoutVersion() throws ValidationException
    {
      Dependency dependency = new Dependency();
      dependency.setGroupId( "group" );
      dependency.setArtifactId( "artifact" );
      dependency.validate( "2.0" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidationExceptionNullId() throws ValidationException
    {
      // groupId and artifactId must not be null
      Dependency dependency = new Dependency();
      dependency.validate( "1.0" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidationExceptionEmptyId() throws ValidationException
    {
      // symbols are not allowed in groupId and artifactId
      Dependency dependency = new Dependency();
      dependency.setGroupId( "" );
      dependency.setArtifactId( "" );
      dependency.validate( "1.0" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidationExceptionWrongGroupId() throws ValidationException
    {
      // symbols are not allowed in groupId and artifactId
      Dependency dependency = new Dependency();
      dependency.setGroupId( "group!" );
      dependency.setArtifactId( "artifact" );
      dependency.validate( "1.0" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidationExceptionWrongArtifactId() throws ValidationException
    {
      // symbols are not allowed in groupId and artifactId
      Dependency dependency = new Dependency();
      dependency.setGroupId( "group" );
      dependency.setArtifactId( "artifact*" );
      dependency.validate( "1.0" );
    }

}
