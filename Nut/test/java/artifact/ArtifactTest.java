package nut.artifact;

import nut.artifact.Artifact;
import nut.artifact.InvalidArtifactRTException;

import nut.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class ArtifactTest
{
    private Artifact artifact;
    private String home;
    private String basedir;
    private String groupId = "groupid", artifactId = "artifactId", version = "1.0", type = "type";
    private String LOCAL_REPO;
    private String LOCAL_FILE;

    @BeforeMethod
    private void before()
    {
        home = System.getProperty( "nut.home" );
        basedir = System.getProperty( "basedir" );
        LOCAL_REPO = basedir + "/target/repository";
        LOCAL_FILE = basedir + "/target/repository/groupid/artifactId-1.0.type";
        new File( LOCAL_REPO ).mkdir();
        new File( LOCAL_REPO + "/groupid" ).mkdir();
        System.setProperty( "nut.home", LOCAL_REPO );
        Log log = new Log();
        log.info("basedir=" + basedir);
    }

    @AfterMethod
    private void after()
    {
        System.setProperty( "nut.home", home );
    }

    @Test
    public void testGetRepository()
    {
        basedir = System.getProperty( "basedir" );
        artifact = new Artifact( groupId, artifactId, version, type );
        assertEquals( basedir + "/target/repository", artifact.getRepository() );
    }

    @Test
    public void testGetPath()
    {
        artifact = new Artifact( groupId, artifactId, version, type );
        assertEquals( groupId + File.separator + artifactId + "-" + version + "." + type, artifact.getPath() );
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
        File target = new File( LOCAL_FILE );
        target.createNewFile();
        artifact = new Artifact( groupId, artifactId, version, type );
        assertTrue( artifact.isPresent() );
        target.delete();
    }

    @Test
    public void testFileInputStream()
              throws IOException
    {
        File target = new File( LOCAL_FILE );
        target.createNewFile();
        FileOutputStream ft = new FileOutputStream(target);
        ft.write( 33 );
        ft.close();
        artifact = new Artifact( groupId, artifactId, version, type );
        FileInputStream f = artifact.fileInputStream();
        int c = f.read();
        f.close();
        target.delete();
        assertEquals( c, 33 );
    }

    @Test
    public void testFileOutputStream()
              throws IOException
    {
        File target = new File( LOCAL_FILE );
        target.createNewFile();
        artifact = new Artifact( groupId, artifactId, version, type );
        FileOutputStream f = artifact.fileOutputStream();
        // Test is OK when write is OK, no exception
        f.write( 33 );
        f.close();
        target.delete();
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
        Properties sysProps = System.getProperties();
        sysProps.remove("nut.home");
        try {
          new Artifact( groupId, artifactId, version, type );
        }
        catch ( InvalidArtifactRTException e ) {
          throw new InvalidArtifactRTException( "The 'nut.home' property is undefined." );
        }
    }

}
