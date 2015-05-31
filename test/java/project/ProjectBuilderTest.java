package nut.project;

import java.io.File;
import java.io.IOException;

import nut.logging.Log;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class ProjectBuilderTest 
{
    @Test(enabled=false)
    public void testProperties()
        throws ProjectBuildingException
    {
        ProjectBuilder builder = new ProjectBuilder();
        NutProject project = builder.build( new File("test/resources/project/nut.xml") );
        assertEquals( "1.1", project.getVersion() );
        assertEquals( "artifact", project.getArtifactId() );
        assertEquals( "test.groupId", project.getGroupId() );
    }
}
