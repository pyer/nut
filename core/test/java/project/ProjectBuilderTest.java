package nut.project;

import java.io.File;
import java.io.IOException;

import nut.logging.Log;

import junit.framework.TestCase;

public class ProjectBuilderTest 
    extends TestCase
{
    public void testProperties()
        throws ProjectBuildingException
    {
        ProjectBuilder builder = new ProjectBuilder( new Log() );
        NutProject project = builder.build( new File("test/resources/project/nut.xml") );
        assertEquals( "1.1", project.getVersion() );
        assertEquals( "artifact", project.getArtifactId() );
        assertEquals( "test.groupId", project.getGroupId() );
    }
}
