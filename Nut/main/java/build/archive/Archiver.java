package nut.build.archive;

import nut.build.archive.ArchiverException;
//import nut.project.Project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.zip.*;

public class Archiver
{
    private ZipOutputStream out;
    private File destFile;

    public File getDestFile()
    {
        return destFile;
    }

    public void setDestFile( final File destFile )
    {
        this.destFile = destFile;
        if ( destFile != null ) {
            destFile.getParentFile().mkdirs();
        }
    }

    public void create()
        throws ArchiverException
    {
      if ( destFile == null ) {
          throw new ArchiverException( "destFile is undefined." );
      }
      try {
          FileOutputStream dest = new FileOutputStream( destFile );
          out = new ZipOutputStream( new BufferedOutputStream(dest));
      } catch(FileNotFoundException e) {
          throw new ArchiverException(e.getMessage());
      }
    }

    public void close()
        throws ArchiverException
    {
      try {
          out.close();
          out = null;
      } catch(IOException e) {
          throw new ArchiverException(e.getMessage());
      }
    }

    public void addDirectory( File directory )
        throws ArchiverException
    {
      if ( directory.isDirectory() ) {
          archiveDir(directory.getAbsolutePath());
      } else {
          throw new ArchiverException( directory.getAbsolutePath() + " isn't a directory." );
      }
    }

    public void addFile( String path, File file )
        throws ArchiverException
    {
        archiveFile( path+"/", file.getPath() );
    }

    // ==========================================================================
    // targetDirectory and resourceDirectory are full path names
    private void archiveDir(String sourceDirectory)
        throws ArchiverException
    {
        try {
          archiveFiles( sourceDirectory + File.separator, "" );
        } catch(Exception e) {
          throw new ArchiverException(e.getMessage());
        }
    }

    // This method is called recursively
    // basedir is a full path name, ending with '/',  and path a relative path name, without trailing '/'
    private void archiveFiles( String basedir, String path )
        throws ArchiverException
    {
        // get a list of files from current directory
        File root = new File( basedir + path );
        File[] list = root.listFiles();
        if (list == null) return;

        String fileName;
        for ( File f : list ) {
          if ( path.isEmpty() ) {
            fileName = f.getName();
          } else {
            fileName = path + File.separator + f.getName();
          }

          if ( f.isDirectory() ) {
            archiveFiles( basedir, fileName );
          } else {
            archiveFile( basedir, fileName );
          }
        }
    }

    // basedir is a full path name, ending with '/'
    private void archiveFile( String basedir, String fileName )
        throws ArchiverException
    {
        int BUFFER = 4096;
        try {
          BufferedInputStream origin = null;
          byte data[] = new byte[BUFFER];
          FileInputStream fi = new FileInputStream( basedir + fileName );
          origin = new BufferedInputStream(fi, BUFFER);
          ZipEntry entry = new ZipEntry( fileName );
          out.putNextEntry(entry);
          int count;
          while((count = origin.read(data, 0, BUFFER)) != -1) {
             out.write(data, 0, count);
          }
          origin.close();
        } catch(Exception e) {
          throw new ArchiverException(e.getMessage());
        }
    }

}
