package nut.build.archive;

import nut.build.archive.ArchiverException;

import java.io.File;
import java.util.Properties;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class ArchiverTest
{
    @Test
    public void testDestFile()
        throws ArchiverException
    {
        String zipName = "target/archive/testDestFile.zip";
        String basedir = System.getProperty( "basedir" ) + File.separator;
        Archiver zip = new Archiver();
        File target = new File(basedir+zipName);
        zip.setDestFile( target );
        File targetDir = new File( basedir+"target/archive" );
        assertTrue( targetDir.exists() );
        assertEquals( target.compareTo(zip.getDestFile()), 0 );
    }

    @Test
    public void testAddDirectory()
        throws ArchiverException
    {
        String zipName = "target/archive/testAddDirectory.zip";
        String basedir = System.getProperty( "basedir" ) + File.separator;
        Archiver zip = new Archiver();
        File target = new File(basedir+zipName);
        zip.setDestFile( target );
        zip.create();
        zip.addDirectory( basedir+"test/resources/zip" );
        zip.close();
        assertTrue( target.exists() );
        assertEquals( target.length(), 436 );
    }

    @Test
    public void testAddFile()
        throws ArchiverException
    {
        String zipName = "target/archive/testAddFile.zip";
        String basedir = System.getProperty( "basedir" ) + File.separator;
        Archiver zip = new Archiver();
        File target = new File(basedir+zipName);
        zip.setDestFile( target );
        zip.create();
        zip.addFile( basedir+"test", "resources/parent.xml" );
        zip.close();
        assertTrue( target.exists() );
        assertEquals( target.length(), 267 );
    }

    @Test
    public void testFullArchive()
        throws ArchiverException
    {
        String zipName = "target/archive/testFullArchive.zip";
        String basedir = System.getProperty( "basedir" ) + File.separator;
        Archiver zip = new Archiver();
        File target = new File(basedir+zipName);
        zip.setDestFile( target );
        zip.create();
        zip.addDirectory( basedir+"test/resources/zip" );
        zip.addFile( basedir+"test", "resources/parent.xml" );
        zip.close();
        assertTrue( target.exists() );
        assertEquals( target.length(), 681 );
    }

}
