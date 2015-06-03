package nut.artifact;

import nut.artifact.Artifact;
import java.io.File;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class ArtifactTest
{

    private Artifact artifact;

    private String groupId = "groupid", artifactId = "artifactId", version = "1.0", type = "type";

    @Test
    public void testPath()
    {
        //System.out.println( artifact.getPath() );
        artifact = new Artifact( groupId, artifactId, version, type );
        assertEquals( groupId + File.separator + artifactId + "-" + version + "." + type,
                      artifact.getPath() );
    }

    @Test
    public void testToString()
    {
        artifact = new Artifact( groupId, artifactId, version, type );
        assertEquals( groupId + ":" + artifactId + ":" + type + ":" + version,
                      artifact.toString() );
    }

}
