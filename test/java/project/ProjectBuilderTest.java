package nut.project;

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
        throws BuildFailureException
    {
        ProjectBuilder builder = new ProjectBuilder();
        Project project = builder.build( new File("test/resources/project/nut.xml") );
        assertEquals( "modules", project.getModel().getPackaging() );
    }

//    @Test(enabled=false)
    @Test
    public void testProperties()
        throws BuildFailureException
    {
        ProjectBuilder builder = new ProjectBuilder();
        Project project = builder.build( new File("test/resources/project/nut.xml") );
        assertEquals( "1.1", project.getVersion() );
//        assertEquals( "artifact", project.getArtifactId() );
//        assertEquals( "test.groupId", project.getGroupId() );
    }
}
