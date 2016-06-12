package nut.goals;

import nut.logging.Log;
import nut.project.Project;

import java.io.File;

// for zip
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.*;

import java.lang.Process;
import java.lang.Runtime;

import java.util.Properties;

public class PackZip
{
    /** Instance logger */
    private static Log log;

    // ==========================================================================
    public static void execute( Project project, Properties config )
        throws Exception
    {
        log = new Log();
        Properties pp               = project.getModel().getProperties();
        String basedir              = (String)pp.getProperty( "basedir" );
        String repository           = (String)pp.getProperty( "nut.home" );
        String targetDirectory      = project.getBuild().getTargetDirectory();
        String resourceDirectory    = project.getBuild().getResourceDirectory();

        log.debug( "build.directory           = " + targetDirectory );
        log.debug( "build.resourceDirectory   = " + resourceDirectory );

        String artifactId           = project.getArtifactId();
        String version              = project.getVersion();
        String packaging            = project.getPackaging();
        String artifactFileName     = artifactId + "." + packaging;

        log.debug( "project.artifactId        = " + artifactId );
        log.debug( "project.version           = " + version );
        log.debug( "project.packaging         = " + packaging );

        log.info( "Packaging \'" + artifactFileName + "\'" );

        if ( artifactId==null || (artifactId.trim().isEmpty() ) ) {
            log.error( "\'project.artifactId\' property is undefined" );
            throw new Exception();
        }

        File targetDir = new File( basedir + File.separator + targetDirectory );
        if ( !targetDir.exists() ) {
            targetDir.mkdirs();
        }
        // zip src/resources/* to target/artifact.zip
        zip( artifactFileName, basedir + File.separator + targetDirectory, basedir + File.separator + resourceDirectory );
    }

    // ==========================================================================
    static final int BUFFER = 2048;

    // targetDirectory and resourceDirectory are full path names
    private static void zip(String finalName, String targetDirectory, String resourceDirectory)
        throws Exception
    {
        try {
          BufferedInputStream origin = null;
          FileOutputStream dest = new FileOutputStream( targetDirectory + File.separator + finalName );
          ZipOutputStream  out  = new ZipOutputStream( new BufferedOutputStream(dest));
          zipFile( out, resourceDirectory + File.separator, "" );
          out.close();
        }
        catch(Exception e) {
            log.error( "Failed to zip. Reason: " + e.getMessage(), e );
            throw new Exception();
        }
    }

    // This method is called recursively
    // basedir is a full path name, ending with '/',  and path a relative path name, without trailing '/'
    private static void zipFile( ZipOutputStream out, String basedir, String path )
        throws Exception
    {
        try {
          BufferedInputStream origin = null;
          byte data[] = new byte[BUFFER];
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
              zipFile( out, basedir, fileName );
            } else {
              log.info("   zipping " + fileName );
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
            log.error( "Failed to zip. Reason: " + e.getMessage(), e );
            throw new Exception();
        }
    }

}
