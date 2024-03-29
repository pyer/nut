package nut.model;

import nut.annotations.Test;
import java.io.File;

import static nut.Assert.*;

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
    public void testProjectPath()
    {
        Project project = new Project();
        project.setGroup("nut.group");
        project.setName("artifact");
        project.setVersion("1.0");
        project.setPackaging("zip");
        assertEquals( project.getPath(), "/nut/group/artifact-1.0.zip" );
    }

    @Test
    public void testProjectVersion()
    {
        Project project = new Project();
        project.setVersion("1.0");
        assertEquals( project.getVersion(), "1.0" );
        project.setVersionMode("-SNAPSHOT");
        assertEquals( project.getVersion(), "1.0-SNAPSHOT" );
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
        String basedir = System.getProperty("nut.basedir", ".");
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
    }

    @Test(expectedExceptions = ParserException.class)
    public void testParseAbsentFile() throws ParserException
    {
        String basedir = System.getProperty("nut.basedir", ".");
        Project project = new Project();
        project.setBaseDirectory(basedir);
        File nut = new File( basedir + "/test/resources/NOFILE" );
        project.parseFile(nut);
    }

    @Test
    public void testParseEmptyFile() throws ParserException
    {
        String basedir = System.getProperty("nut.basedir", ".");
        Project project = new Project();
        project.setBaseDirectory(basedir);
        File nut = new File( basedir + "/test/resources/emptyProject.yaml" );
        project.parseFile(nut);
        assertEquals( project.getPath(), "//-.jar" );
    }

    @Test
    public void testParseSmallProject() throws ParserException
    {
        String basedir = System.getProperty("nut.basedir", ".");
        Project project = new Project();
        project.setBaseDirectory(basedir);
        project.setRepository( basedir + "/test/resources" );
        File nut = new File( basedir + "/test/resources/smallProject.yaml" );
        project.parseFile(nut);

        assertEquals( project.getPath(), "/nut/test/small-3.0.jar" );
    }

    @Test
    public void testParseFullProject() throws ParserException
    {
        String basedir = System.getProperty("nut.basedir", ".");
        Project project = new Project();
        project.setBaseDirectory(basedir);
        File nut = new File( basedir + "/test/resources/fullProject.yaml" );
        project.parseFile(nut);
        assertEquals( project.getPath(), "/nut/test/full-3.0.jar" );
        assertEquals( project.getSourceDirectory(),       "target/main/java" );
        assertEquals( project.getResourceDirectory(),     "target/main/resources" );
        assertEquals( project.getWebappDirectory(),       "target/main/webapp" );
        assertEquals( project.getTestSourceDirectory(),   "target/test/java" );
        assertEquals( project.getTestResourceDirectory(), "target/test/resources" );
        assertEquals( project.getTargetDirectory(),       "target/test-target" );
        assertEquals( project.getOutputDirectory(),       "target/test-target/classes" );
        assertEquals( project.getTestOutputDirectory(),   "target/test-target/test-classes" );
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
        String basedir = System.getProperty("nut.basedir", ".");
        Project project = new Project();
        project.setBaseDirectory(basedir);
        File nut = new File( basedir + "/test/resources/emptyProject.yaml" );
        project.parseFile(nut);
        project.validate();
    }

    @Test
    public void testValidateSmallProject() throws ParserException, ValidationException
    {
        String basedir = System.getProperty("nut.basedir", ".");
        Project project = new Project();
        project.setBaseDirectory(basedir);
        project.setRepository( basedir + "/test/resources" );
        File nut = new File( basedir + "/test/resources/smallProject.yaml" );
        project.parseFile(nut);
        project.validate();
    }

    @Test
    public void testValidateFullProject() throws ParserException, ValidationException
    {
        String basedir = System.getProperty("nut.basedir", ".");
        Project project = new Project();
        project.setBaseDirectory(basedir);
        project.setRepository( basedir + "/test/resources" );
        File nut = new File( basedir + "/test/resources/fullProject.yaml" );
        project.parseFile(nut);
        project.validate();
    }

    @Test
    public void testState()
    {
        Project project = new Project();
        assertTrue( project.isNotVisited() );
        project.visiting();
        assertTrue( project.isVisiting() );
        project.visited();
        assertTrue( project.isVisited() );
    }

}
