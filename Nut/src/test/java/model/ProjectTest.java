package nut.model;

import java.io.File;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ProjectTest
{
    @Test
    public void testHashCodeNullSafe()
    {
        new Project().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertNotNull( new Project() );
    }

    @Test
    public void testEqualsIdentity()
    {
        Project thing = new Project();
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Project().toString() );
    }

    @Test
    public void testProjectId()
    {
        Project project = new Project();
        project.setGroup("nut.group");
        project.setName("artifact");
        project.setVersion("1.0");
        assertEquals( project.getId(), "nut.group:artifact:1.0" );
    }

    @Test
    public void testProjectPathName()
    {
        Project project = new Project();
        project.setGroup("nut.group");
        project.setName("artifact");
        project.setVersion("1.0");
        project.setPackaging("zip");
        assertEquals( project.getPathName(), "/nut/group/artifact-1.0.zip" );
    }

    @Test
    public void testProjectDefaultPackaging()
    {
        Project project = new Project();
        assertEquals( project.getPackaging(), "jar" );
    }

    @Test
    public void testProjectDefaultPattern()
    {
        Project project = new Project();
        assertEquals( project.getPattern(), "modules" );
    }

    @Test
    public void testProjectDefaultTestSuite()
    {
        Project project = new Project();
        assertEquals( project.getTestSuite(), "src/test/testng.xml" );
    }

    @Test
    public void testProjectDefaultDirectories()
    {
        Project project = new Project();
        assertEquals( project.getSourceDirectory(), "src/main/java" );
        assertEquals( project.getResourceDirectory(), "src/main/resources" );
        assertEquals( project.getWebappDirectory(), "src/main/webapp" );
        assertEquals( project.getTestSourceDirectory(), "src/test/java" );
        assertEquals( project.getTestResourceDirectory(), "src/test/resources" );
        assertEquals( project.getTargetDirectory(), "target" );
        assertEquals( project.getOutputDirectory(), "target/classes" );
        assertEquals( project.getTestOutputDirectory(), "target/test-classes" );
        assertEquals( project.getTestReportDirectory(), "target/test-reports" );
    }

    @Test(expectedExceptions = ParserException.class)
    public void testParseAbsentFile() throws ParserException
    {
        Project project = new Project();
        File nut = new File( "src/test/resources/NOFILE" );
        project.parseFile(nut);
    }

    @Test
    public void testParseEmptyFile() throws ParserException
    {
        Project project = new Project();
        File nut = new File( "src/test/resources/emptyProject.yml" );
        project.parseFile(nut);
        assertEquals( project.getId(), "null:null:null" );
    }

    @Test
    public void testParseSmallProject() throws ParserException
    {
        Project project = new Project();
        project.setRepository( project.getBaseDirectory() + "/src/test/resources" );
        File nut = new File( "src/test/resources/smallProject.yml" );
        project.parseFile(nut);

        assertEquals( project.getId(), "nut.test:small:3.0" );
    }

    @Test
    public void testParseFullProject() throws ParserException
    {
        Project project = new Project();
        File nut = new File( "src/test/resources/fullProject.yml" );
        project.parseFile(nut);
        assertEquals( project.getId(), "nut.test:full:3.0" );
        assertEquals( project.getSourceDirectory(), "main/java" );
        assertEquals( project.getResourceDirectory(), "main/resources" );
        assertEquals( project.getWebappDirectory(), "main/webapp" );
        assertEquals( project.getTestSourceDirectory(), "test/java" );
        assertEquals( project.getTestResourceDirectory(), "test/resources" );
        assertEquals( project.getTargetDirectory(), "tget" );
        assertEquals( project.getOutputDirectory(), "tget/classes" );
        assertEquals( project.getTestOutputDirectory(), "tget/test-classes" );
        assertEquals( project.getTestReportDirectory(), "tget/test-reports" );
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateEmptyProject() throws ValidationException
    {
        Project project = new Project();
        project.validate();
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateEmptyFile() throws ParserException, ValidationException
    {
        Project project = new Project();
        File nut = new File( "src/test/resources/emptyProject.yml" );
        project.parseFile(nut);
        project.validate();
    }

    @Test
    public void testValidateSmallProject() throws ParserException, ValidationException
    {
        Project project = new Project();
        project.setRepository( project.getBaseDirectory() + "/src/test/resources" );
        File nut = new File( "src/test/resources/smallProject.yml" );
        project.parseFile(nut);
        project.validate();
    }

    @Test
    public void testValidateFullProject() throws ParserException, ValidationException
    {
        Project project = new Project();
        project.setRepository( project.getBaseDirectory() + "/src/test/resources" );
        File nut = new File( "src/test/resources/fullProject.yml" );
        project.parseFile(nut);
        project.validate();
    }

}
