package nut.artifact;

import nut.artifact.Artifact;
import java.io.File;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class ArtifactTest
{

    private Artifact artifact;

    private String groupId = "groupid", artifactId = "artifactId", version = "1.0", type = "type", classifier = "classifier";

    @Test
    public void testPath()
    {
        //System.out.println( artifact.getPath() );
        artifact = new Artifact( groupId, artifactId, version, type, classifier );
        assertEquals( groupId + File.separator + artifactId + "-" + version + "-" + classifier + "." + type,
                      artifact.getPath() );
    }

    @Test
    public void testToString()
    {
        artifact = new Artifact( groupId, artifactId, version, type, classifier );
        assertEquals( groupId + ":" + artifactId + ":" + type + ":" + classifier + ":" + version,
                      artifact.toString() );
    }

    @Test
    public void testToStringNullClassifier()
    {
        artifact = new Artifact( groupId, artifactId, version, type, null );
        assertEquals( groupId + ":" + artifactId + ":" + type + ":" + version, artifact.toString() );
    }
}
