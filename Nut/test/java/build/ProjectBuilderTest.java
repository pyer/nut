package nut.build;

import nut.build.ProjectBuilder;
import nut.build.BuildException;
import nut.project.Project;

import java.io.File;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

public class ProjectBuilderTest
{
    private String home;

    @BeforeTest
    public void before() {
      home = System.getProperty( "nut.home", "." );
      String basedir = System.getProperty( "basedir" );
      System.setProperty( "nut.home", basedir + "/test/resources" );
    }

    @AfterTest
    public void after() {
      System.setProperty( "nut.home", home );
    }

//    @Test(enabled=false)
    @Test
    public void testHashCodeNullSafe() {
      new ProjectBuilder().hashCode();
    }

    @Test(expectedExceptions = BuildException.class)
    public void testUnknownProjectFile() throws BuildException {
      ProjectBuilder builder = new ProjectBuilder();
      String basedir = System.getProperty( "basedir" );
      builder.launch( new File(basedir + "/test/resources/no.xml") );
    }

    @Test
    public void testNutHomeProperty() throws BuildException {
      ProjectBuilder builder = new ProjectBuilder();
      String basedir = System.getProperty( "basedir" );
      Project project = builder.launch( new File(basedir + "/test/resources/project/nut.xml") );
      String value = project.getModel().getProperties().getProperty( "nut.home" );
      assertTrue( value.length() > 2 );
    }

    @Test
    public void testDefaultPackaging() throws BuildException {
        ProjectBuilder builder = new ProjectBuilder();
        String basedir = System.getProperty( "basedir" );
        Project project = builder.launch( new File(basedir + "/test/resources/project/nut.xml") );
        assertEquals( "modules", project.getModel().getPackaging() );
    }

    @Test(expectedExceptions = BuildException.class)
    public void testUnkownPackaging() throws BuildException {
        ProjectBuilder builder = new ProjectBuilder();
        String basedir = System.getProperty( "basedir" );
        builder.launch( new File(basedir + "/test/resources/project/unknownpackaging.xml") );
    }

    @Test
    public void testProjectArtifact() throws BuildException {
        ProjectBuilder builder = new ProjectBuilder();
        String basedir = System.getProperty( "basedir" );
        Project project = builder.launch( new File(basedir + "/test/resources/project/nut.xml") );
        assertEquals( "1.1", project.getVersion() );
        assertEquals( "artifact", project.getArtifactId() );
        assertEquals( "test.groupId", project.getGroupId() );
    }

    @Test
    public void testMergedParent() throws BuildException {
        ProjectBuilder builder = new ProjectBuilder();
        String basedir = System.getProperty( "basedir" );
        Project project = builder.launch( new File(basedir + "/test/resources/project/child.xml") );
        assertEquals( "1.1", project.getVersion() );
        assertEquals( "artifact", project.getArtifactId() );
        assertEquals( "test.groupId", project.getGroupId() );
    }

    @Test
    public void testNullVersion() throws BuildException {
        ProjectBuilder builder = new ProjectBuilder();
        String basedir = System.getProperty( "basedir" );
        Project project = builder.launch( new File(basedir + "/test/resources/nullVersion.xml") );
        assertEquals( "1.1", project.getVersion() );
    }
}
