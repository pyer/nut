package nut.artifact;

import nut.artifact.Artifact;
import junit.framework.TestCase;
import java.io.File;

public class ArtifactTest
    extends TestCase
{

    private Artifact artifact;

    private String groupId = "groupid", artifactId = "artifactId", version = "1.0", type = "type", classifier = "classifier";

    protected void setUp()
        throws Exception
    {
        super.setUp();
        artifact = new Artifact( groupId, artifactId, version, type, classifier );
    }
/*
    public void testGetDependencyConflictId()
    {
        assertEquals( groupId + ":" + artifactId + ":" + type + ":" + classifier, artifact.getDependencyConflictId() );
    }

    public void testGetDependencyConflictIdNullClassifier()
    {
        artifact = new Artifact( groupId, artifactId, version, type, null );
        assertEquals( groupId + ":" + artifactId + ":" + type, artifact.getDependencyConflictId() );
    }
*/
    public void testResolvedTrue()
    {
        artifact = new Artifact( groupId, artifactId, version, type, null );
        artifact.setResolved(true);
        assertTrue( artifact.isResolved());
    }

    public void testResolvedFalse()
    {
        artifact = new Artifact( groupId, artifactId, version, type, null );
        artifact.setResolved(false);
        assertFalse( artifact.isResolved());
    }

    public void testPath()
    {
        //System.out.println( artifact.getPath() );
        assertEquals( groupId + File.separator + artifactId + "-" + version + "-" + classifier + "." + type,
                      artifact.getPath() );
    }

    public void testToString()
    {
        assertEquals( groupId + ":" + artifactId + ":" + type + ":" + classifier + ":" + version,
                      artifact.toString() );
    }

    public void testToStringNullClassifier()
    {
        artifact = new Artifact( groupId, artifactId, version, type, null );
        assertEquals( groupId + ":" + artifactId + ":" + type + ":" + version, artifact.toString() );
    }


}
