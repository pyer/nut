package nut.goals.packs.util;

import nut.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.*;

public class ZipFiles
{
    /** Instance logger */
    private Logger log;

    private String source;
    private String dest;

    // ==========================================================================
    public ZipFiles(String source, String dest)
    {
        this.source = source + File.separator;
        this.dest   = dest;
        log = new Logger();
    }

    public void process() throws IOException
    {
        ZipOutputStream out  = new ZipOutputStream( new BufferedOutputStream( new FileOutputStream(dest)));
        zipFile( out, source, "" );
        out.close();
    }

    // ==========================================================================
    // This method is called recursively
    // basedir is a full path name, ending with '/',  and path a relative path name, without trailing '/'
    private void zipFile( ZipOutputStream out, String basedir, String path ) throws IOException
    {
        int BUFFER = 2048;
        try {
          BufferedInputStream origin = null;
          byte data[] = new byte[BUFFER];
          // get a list of files from current directory
          File root = new File( basedir + path );
          log.info("  scanning " + root);
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
              zipFile( out, basedir, fileName );
            } else {
              log.info("   zipping " + fileName);
              FileInputStream fi = new FileInputStream( basedir + fileName );
              origin = new BufferedInputStream(fi, BUFFER);
              ZipEntry entry = new ZipEntry( fileName );
              out.putNextEntry(entry);
              int count;
              while((count = origin.read(data, 0, BUFFER)) != -1) {
                 out.write(data, 0, count);
              }
              origin.close();
            }
          }
        }
        catch(Exception e) {
            throw new IOException("Failed to zip. Reason: " + e.getMessage());
        }
    }
    // ==========================================================================
}
