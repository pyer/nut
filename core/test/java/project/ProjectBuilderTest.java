package nut.project;

import java.io.File;
import java.io.IOException;

import nut.logging.Log;

import junit.framework.TestCase;

public class ProjectBuilderTest 
    extends TestCase
{
    public void testGroupIdInheritance()
        throws ProjectBuildingException
    {
        ProjectBuilder builder = new ProjectBuilder( new Log() );
        NutProject project = builder.build( new File("test/resources/project/nutGroupId.xml") );
        assertEquals( "test.groupId", project.getGroupId() );
        assertEquals( "artifact", project.getArtifactId() );
    }

    public void testVersionInheritance()
        throws ProjectBuildingException
    {
        ProjectBuilder builder = new ProjectBuilder( new Log() );
        NutProject project = builder.build( new File("test/resources/project/nutVersion.xml") );
        assertEquals( "1.1", project.getVersion() );
        assertEquals( "artifact", project.getArtifactId() );
    }
}
