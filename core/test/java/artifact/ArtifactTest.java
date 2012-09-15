package nut.artifact;

import junit.framework.TestCase;

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
