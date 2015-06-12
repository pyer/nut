package nut.artifact;

import nut.artifact.Artifact;
import nut.artifact.InvalidArtifactRTException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class ArtifactTest
{
  /*
  public Artifact( String groupId, String artifactId, String version, String type )
  public String getGroupId()
  public String getArtifactId()
  public String getVersion()
  public String getType()
  public String getPath()
  public File getFile()
  public String mavenFilePath()
  public String nutFilePath()
  public boolean isPresent()
  public FileInputStream fileInputStream()
  public FileOutputStream fileOutputStream()
  public String toString()
  */

    private Artifact artifact;

    private String groupId = "groupid", artifactId = "artifactId", version = "1.0", type = "type";

    @Test
    public void testGetPath()
    {
        artifact = new Artifact( groupId, artifactId, version, type );
        assertEquals( groupId + File.separator + artifactId + "-" + version + "." + type,
                      artifact.getPath() );
    }

    @Test
    public void testGetFile()
              throws IOException
    {
        String repository = System.getProperty( "nut.home" );
        System.setProperty( "nut.home", "target" );
        File target = new File( "target/groupid/artifactId-1.0.type" );
        artifact = new Artifact( groupId, artifactId, version, type );
        assertEquals( target.getCanonicalPath(), artifact.getFile().getCanonicalPath() );
        System.setProperty( "nut.home", repository );
    }

    @Test
    public void testMavenFilePath()
    {
        artifact = new Artifact( groupId, artifactId, version, type );
        assertEquals( "/groupid/artifactId/1.0/artifactId-1.0.type", artifact.mavenFilePath() );
    }

    @Test
    public void testNutFilePath()
    {
        artifact = new Artifact( groupId, artifactId, version, type );
        assertEquals( "/groupid/artifactId-1.0.type", artifact.nutFilePath() );
    }

    @Test
    public void testIsPresent()
              throws IOException
    {
        String repository = System.getProperty( "nut.home" );
        System.setProperty( "nut.home", "target/repo" );
        new File( "target/repo" ).mkdir();
        File target = new File( "target/repo/groupid/artifactId-1.0.type" );
        new File( "target/repo/groupid" ).mkdir();
        target.createNewFile();
        artifact = new Artifact( groupId, artifactId, version, type );
        assertTrue( artifact.isPresent() );
        target.delete();
        System.setProperty( "nut.home", repository );
    }

    @Test
    public void testFileInputStream()
              throws IOException
    {
        String repository = System.getProperty( "nut.home" );
        System.setProperty( "nut.home", "target/repo" );
        new File( "target/repo" ).mkdir();
        File target = new File( "target/repo/groupid/artifactId-1.0.type" );
        new File( "target/repo/groupid" ).mkdir();
        target.createNewFile();
        FileOutputStream ft = new FileOutputStream(target);
        ft.write( 33 );
        ft.close();
        artifact = new Artifact( groupId, artifactId, version, type );
        FileInputStream f = artifact.fileInputStream();
        int c = f.read();
        f.close();
        target.delete();
        System.setProperty( "nut.home", repository );
        assertEquals( c, 33 );
    }

    @Test
    public void testFileOutputStream()
              throws IOException
    {
        System.setProperty( "nut.home", "target/repo" );
        new File( "target/repo" ).mkdir();
        File target = new File( "target/repo/groupid/artifactId-1.0.type" );
        new File( "target/repo/groupid" ).mkdir();
        String repository = System.getProperty( "nut.home" );
        target.createNewFile();
        artifact = new Artifact( groupId, artifactId, version, type );
        FileOutputStream f = artifact.fileOutputStream();
        // Test is OK when write is OK, no exception
        f.write( 33 );
        f.close();
        target.delete();
        System.setProperty( "nut.home", repository );
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
