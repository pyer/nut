package nut.plugins;

/*
import nut.logging.Log;

import nut.model.Model;
import nut.project.NutProject;
*/
import nut.plugins.release;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Properties;
import java.util.List;
import java.util.ArrayList;


public class releaseTest
    extends TestCase
{

    private File NutFile  = new File( "test/resources/nut.xml" );
    private File TestFile = new File( "target/nut.xml" );
    
    public void setUp()
        throws Exception
    {
        super.setUp();   
        initTestFile();
/*
        Model model = new Model();
        model.addProperty( "basedir",    "target" );
        model.addProperty( "repository", LOCAL_REPO );
        model.addProperty( "build.directory",    "build" );
        model.addProperty( "project.groupId",    "local.group" );
        model.addProperty( "project.artifactId", "artifact" );
        model.addProperty( "project.version",    "0.0-SNAPSHOT" );
        model.addProperty( "project.packaging",  "file" );
        project = new NutProject(model);
*/
    }

    public void testIfNutExists()
    {
        assertTrue( TestFile.exists() );
    }

    public void testDefaultVersion()
    {
        assertEquals( "1.0-SNAPSHOT", release.getNutVersion( TestFile ) );
    }

    public void testIncrementVersion0()
    {
        release.setSnapshotVersion(TestFile);
        assertEquals( "1.0-SNAPSHOT", release.getNutVersion( TestFile ) );
        release.incrementVersion(TestFile);
        assertEquals( "1.0", release.getNutVersion( TestFile ) );
    }

    public void testIncrementVersion1()
    {
        release.setNutVersion(TestFile,"1.1");
        assertEquals( "1.1", release.getNutVersion( TestFile ) );
        release.incrementVersion(TestFile);
        assertEquals( "1.2-SNAPSHOT", release.getNutVersion( TestFile ) );
    }

    public void testIncrementVersion9()
    {
        release.setNutVersion(TestFile,"1.9");
        assertEquals( "1.9", release.getNutVersion( TestFile ) );
        release.incrementVersion(TestFile);
        assertEquals( "1.10-SNAPSHOT", release.getNutVersion( TestFile ) );
    }

    public void testReleaseToSnapshot()
    {
        release.setReleaseVersion(TestFile);
        assertEquals( "1.0", release.getNutVersion( TestFile ) );
        release.setSnapshotVersion(TestFile);
        assertEquals( "1.0-SNAPSHOT", release.getNutVersion( TestFile ) );
    }

    public void testReleaseToRelease()
    {
        release.setReleaseVersion(TestFile);
        assertEquals( "1.0", release.getNutVersion( TestFile ) );
        release.setReleaseVersion(TestFile);
        assertEquals( "1.0", release.getNutVersion( TestFile ) );
    }

    public void testSnapshotToSnapshot()
    {
        release.setSnapshotVersion(TestFile);
        assertEquals( "1.0-SNAPSHOT", release.getNutVersion( TestFile ) );
        release.setSnapshotVersion(TestFile);
        assertEquals( "1.0-SNAPSHOT", release.getNutVersion( TestFile ) );
    }

    public void testSnapshotToRelease()
    {
        release.setSnapshotVersion(TestFile);
        assertEquals( "1.0-SNAPSHOT", release.getNutVersion( TestFile ) );
        release.setReleaseVersion(TestFile);
        assertEquals( "1.0", release.getNutVersion( TestFile ) );
    }

    private void initTestFile()
        throws Exception
    {
    // NutFile = "test/resources/nut.xml";
    // TestFile = "target/nut.xml";
        if( TestFile.exists() ) {
          TestFile.delete();
        }
        FileWriter fw = new FileWriter(TestFile); 
        BufferedWriter bw = new BufferedWriter(fw);
        FileReader fr = new FileReader(NutFile);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null)
        {
          bw.write(line + "\n");
        }
        br.close();
        bw.close();
    }
}
