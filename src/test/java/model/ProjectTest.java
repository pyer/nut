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
        project.setPackaging("zip");
        assertEquals( project.getId(), "nut.group:artifact:1.0:zip" );
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
    public void testProjectDefaultDirectories()
    {
        String basedir = System.getProperty( "basedir", "." );
        Project project = new Project();
        project.setBaseDirectory(basedir);
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
        String basedir = System.getProperty( "basedir", "." );
        Project project = new Project();
        project.setBaseDirectory(basedir);
        File nut = new File( basedir + "/src/test/resources/NOFILE" );
        project.parseFile(nut);
    }

    @Test
    public void testParseEmptyFile() throws ParserException
    {
        String basedir = System.getProperty( "basedir", "." );
        Project project = new Project();
        project.setBaseDirectory(basedir);
        File nut = new File( basedir + "/src/test/resources/emptyProject.yml" );
        project.parseFile(nut);
        assertEquals( project.getId(), "null:null:null:jar" );
    }

    @Test
    public void testParseSmallProject() throws ParserException
    {
        String basedir = System.getProperty( "basedir", "." );
        Project project = new Project();
        project.setBaseDirectory(basedir);
        project.setRepository( basedir + "/src/test/resources" );
        File nut = new File( basedir + "/src/test/resources/smallProject.yml" );
        project.parseFile(nut);

        assertEquals( project.getId(), "nut.test:small:3.0:jar" );
    }

    @Test
    public void testParseFullProject() throws ParserException
    {
        String basedir = System.getProperty( "basedir", "." );
        Project project = new Project();
        project.setBaseDirectory(basedir);
        File nut = new File( basedir + "/src/test/resources/fullProject.yml" );
        project.parseFile(nut);
        assertEquals( project.getId(), "nut.test:full:3.0:jar" );
        assertEquals( project.getSourceDirectory(),       "target/main/java" );
        assertEquals( project.getResourceDirectory(),     "target/main/resources" );
        assertEquals( project.getWebappDirectory(),       "target/main/webapp" );
        assertEquals( project.getTestSourceDirectory(),   "target/test/java" );
        assertEquals( project.getTestResourceDirectory(), "target/test/resources" );
        assertEquals( project.getTargetDirectory(),       "target/test-target" );
        assertEquals( project.getOutputDirectory(),       "target/test-target/classes" );
        assertEquals( project.getTestOutputDirectory(),   "target/test-target/test-classes" );
        assertEquals( project.getTestReportDirectory(),   "target/test-target/test-reports" );
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
        String basedir = System.getProperty( "basedir", "." );
        Project project = new Project();
        project.setBaseDirectory(basedir);
        File nut = new File( basedir + "/src/test/resources/emptyProject.yml" );
        project.parseFile(nut);
        project.validate();
    }

    @Test
    public void testValidateSmallProject() throws ParserException, ValidationException
    {
        String basedir = System.getProperty( "basedir", "." );
        Project project = new Project();
        project.setBaseDirectory(basedir);
        project.setRepository( basedir + "/src/test/resources" );
        File nut = new File( basedir + "/src/test/resources/smallProject.yml" );
        project.parseFile(nut);
        project.validate();
    }

    @Test
    public void testValidateFullProject() throws ParserException, ValidationException
    {
        String basedir = System.getProperty( "basedir", "." );
        Project project = new Project();
        project.setBaseDirectory(basedir);
        project.setRepository( basedir + "/src/test/resources" );
        File nut = new File( basedir + "/src/test/resources/fullProject.yml" );
        project.parseFile(nut);
        project.validate();
    }

}
