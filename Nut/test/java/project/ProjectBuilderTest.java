package nut.project;

import nut.project.Project;
import nut.project.ProjectBuilder;
import nut.project.BuildException;
import nut.logging.Log;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class ProjectBuilderTest
{
    @Test
    public void testHashCodeNullSafe() {
        new ProjectBuilder().hashCode();
    }

    @Test
    public void testDefaultPackaging()
        throws BuildException
    {
        ProjectBuilder assembler = new ProjectBuilder();
        String basedir = System.getProperty( "basedir" );
        Project project = assembler.build( new File(basedir + "/test/resources/project/nut.xml") );
        assertEquals( "modules", project.getModel().getPackaging() );
    }

//    @Test(enabled=false)
    @Test
    public void testProperties()
        throws BuildException
    {
        ProjectBuilder assembler = new ProjectBuilder();
        String basedir = System.getProperty( "basedir" );
        Project project = assembler.build( new File(basedir + "/test/resources/project/nut.xml") );
        assertEquals( "1.1", project.getVersion() );
//        assertEquals( "artifact", project.getArtifactId() );
//        assertEquals( "test.groupId", project.getGroupId() );
    }

    @Test
    public void testNullVersion()
        throws BuildException
    {
        ProjectBuilder assembler = new ProjectBuilder();
        String basedir = System.getProperty( "basedir" );
        Project project = assembler.build( new File(basedir + "/test/resources/nullVersion.xml") );
        assertEquals( "1.1", project.getVersion() );
    }

}
