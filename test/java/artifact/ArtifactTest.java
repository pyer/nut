package nut.artifact;

import nut.artifact.Artifact;
import nut.artifact.InvalidArtifactRTException;

import java.io.File;
import java.util.Properties;

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


    @Test(expectedExceptions = InvalidArtifactRTException.class)
    public void testNullGroupId() throws InvalidArtifactRTException
    {
        new Artifact( null, artifactId, version, type );
    }

    @Test(expectedExceptions = InvalidArtifactRTException.class)
    public void testEmptyGroupId() throws InvalidArtifactRTException
    {
        new Artifact( "", artifactId, version, type );
    }

    @Test(expectedExceptions = InvalidArtifactRTException.class)
    public void testNullArtifactId() throws InvalidArtifactRTException
    {
        new Artifact( groupId, null, version, type );
    }

    @Test(expectedExceptions = InvalidArtifactRTException.class)
    public void testEmptyArtifactId() throws InvalidArtifactRTException
    {
        new Artifact( groupId, "", version, type );
    }

    @Test(expectedExceptions = InvalidArtifactRTException.class)
    public void testNullVersion() throws InvalidArtifactRTException
    {
        new Artifact( groupId, artifactId, null, type );
    }

    @Test(expectedExceptions = InvalidArtifactRTException.class)
    public void testEmptyVersion() throws InvalidArtifactRTException
    {
        new Artifact( groupId, artifactId, "", type );
    }

    @Test(expectedExceptions = InvalidArtifactRTException.class)
    public void testNullType() throws InvalidArtifactRTException
    {
        new Artifact( groupId, artifactId, version, null );
    }

    @Test(expectedExceptions = InvalidArtifactRTException.class)
    public void testEmptyType() throws InvalidArtifactRTException
    {
        new Artifact( groupId, artifactId, version, "" );
    }

    @Test(expectedExceptions = InvalidArtifactRTException.class)
    public void testNullRepository() throws InvalidArtifactRTException
    {
        String repository = System.getProperty( "nut.home" );
        Properties sysProps = System.getProperties();
        sysProps.remove("nut.home");
        try {
          new Artifact( groupId, artifactId, version, type );
        }
        catch ( InvalidArtifactRTException e ) {
          System.setProperty( "nut.home", repository );
          throw new InvalidArtifactRTException( "The 'nut.home' property is undefined." );
        }
        finally {
          System.setProperty( "nut.home", repository );
        }
    }

}
