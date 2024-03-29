package nut.goals.packs;

import nut.Logger;
import nut.goals.GoalException;
import nut.model.Project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.*;

public class Zip
{
    /** Instance logger */
    private Logger log;
    private String name;

    // ==========================================================================
    public Zip( String name )
    {
        this.name = name;
        log = new Logger();
    }

    // ==========================================================================
    public void archive(Project project) throws GoalException
    {
        String resourceDirectory = project.getBaseDirectory() + File.separator + project.getResourceDirectory();
        log.debug( "resourceDirectory   = " + resourceDirectory );
        try {
          FileOutputStream dest = new FileOutputStream( this.name );
          ZipOutputStream  out  = new ZipOutputStream( new BufferedOutputStream(dest));
          zipFile( out, resourceDirectory + File.separator, "" );
          out.close();
        }
        catch(Exception e) {
            throw new GoalException(e.getMessage());
        }
    }

    // ==========================================================================
    // This method is called recursively
    // basedir is a full path name, ending with '/',  and path a relative path name, without trailing '/'
    private void zipFile( ZipOutputStream out, String basedir, String path ) throws GoalException
    {
        int BUFFER = 2048;
        try {
          BufferedInputStream origin = null;
          byte data[] = new byte[BUFFER];
          // get a list of files from current directory
          File root = new File( basedir + path );
          log.debug("  scanning " + root);
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
              log.info("  - " + fileName);
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
            throw new GoalException("Failed to zip. Reason: " + e.getMessage());
        }
    }
    // ==========================================================================
}
