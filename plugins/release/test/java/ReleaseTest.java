package nut.plugins;

/*
import nut.logging.Log;
import nut.model.Model;
import nut.project.NutProject;
*/
import nut.plugins.release;

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

import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;

public class ReleaseTest
{
    private File TestFile = new File( "target/nut.xml" );
    
    @BeforeTest
    public void setup()
        throws IOException
    {
        if( TestFile.exists() ) {
          TestFile.delete();
        }
        FileWriter fw = new FileWriter(TestFile); 
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("<project>\n");
        bw.write("  <groupId>nut.plugins</groupId>\n");
        bw.write("  <artifactId>versionning</artifactId>\n");
        bw.write("  <version>1.0-SNAPSHOT</version>\n");
        bw.write("  <packaging>jar</packaging>\n");
        bw.write("</project>\n");
        bw.close();
    }

    @Test
    public void testIfNutExists()
    {
        assertTrue( TestFile.exists() );
    }

    @Test
    public void testDefaultVersion()
    {
        assertEquals( "1.0-SNAPSHOT", release.getNutVersion( TestFile ) );
    }

    @Test
    public void testIncrementVersion0()
    {
        release.setSnapshotVersion(TestFile);
        assertEquals( "1.0-SNAPSHOT", release.getNutVersion( TestFile ) );
        release.incrementVersion(TestFile);
        assertEquals( "1.0", release.getNutVersion( TestFile ) );
    }

    @Test
    public void testIncrementVersion1()
    {
        release.setNutVersion(TestFile,"1.1");
        assertEquals( "1.1", release.getNutVersion( TestFile ) );
        release.incrementVersion(TestFile);
        assertEquals( "1.2-SNAPSHOT", release.getNutVersion( TestFile ) );
    }

    @Test
    public void testIncrementVersion9()
    {
        release.setNutVersion(TestFile,"1.9");
        assertEquals( "1.9", release.getNutVersion( TestFile ) );
        release.incrementVersion(TestFile);
        assertEquals( "1.10-SNAPSHOT", release.getNutVersion( TestFile ) );
    }

    @Test
    public void testReleaseToSnapshot()
    {
        release.setNutVersion(TestFile,"1.0");
        assertEquals( "1.0", release.getNutVersion( TestFile ) );
        release.setSnapshotVersion(TestFile);
        assertEquals( "1.0-SNAPSHOT", release.getNutVersion( TestFile ) );
    }

    @Test
    public void testReleaseToRelease()
    {
        release.setNutVersion(TestFile,"1.0");
        assertEquals( "1.0", release.getNutVersion( TestFile ) );
        release.setReleaseVersion(TestFile);
        assertEquals( "1.0", release.getNutVersion( TestFile ) );
    }

    @Test
    public void testSnapshotToSnapshot()
    {
        release.setNutVersion(TestFile,"1.0-SNAPSHOT");
        assertEquals( "1.0-SNAPSHOT", release.getNutVersion( TestFile ) );
        release.setSnapshotVersion(TestFile);
        assertEquals( "1.0-SNAPSHOT", release.getNutVersion( TestFile ) );
    }

    @Test
    public void testSnapshotToRelease()
    {
        release.setNutVersion(TestFile,"1.0-SNAPSHOT");
        assertEquals( "1.0-SNAPSHOT", release.getNutVersion( TestFile ) );
        release.setReleaseVersion(TestFile);
        assertEquals( "1.0", release.getNutVersion( TestFile ) );
    }
}
