package nut.project;

import nut.artifact.Artifact;
import nut.model.Repository;

import nut.logging.Log;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Check if a dependency artifact file is present in the local repository.
 * if not, seeks artifact in central repositories
 */
public class DependencyChecker
{
    private Log log;
    // ----------------------------------------------------------------------
    public DependencyChecker( Artifact artifact, List<Repository> repositories )
        throws DependencyNotFoundException
    {
      log = new Log();
      try {
        if( !artifact.isPresent() ) {
          for ( Iterator iter = repositories.iterator(); iter.hasNext(); ) {
            Repository repo = (Repository) iter.next();
            log.debug( "* search " + artifact.toString() + " in " + repo.getName() );
            download( artifact, repo );
            if( artifact.isPresent() ) {
              return;
            }
          }
        }
      } catch (SecurityException e) {
        throw new DependencyNotFoundException( "Artifact '" + artifact.toString() + "' is unreadable." );
      } catch (Exception e) {
        throw new DependencyNotFoundException( "Artifact '" + artifact.toString() + "' " + e.getMessage() );
      }

      if( !artifact.isPresent() ) {
              throw new DependencyNotFoundException( "Artifact '" + artifact.toString() + "' is not found." );
      }
    }

    // ----------------------------------------------------------------------
    private void download( Artifact artifact, Repository repository )
        throws Exception
    {
      URL url;
      InputStream    is;
      FileOutputStream fos;
      // Use this for reading the data.
      byte[] buffer = new byte[8000];
      String s;
      String request;
      if ( "nut".equals( repository.getLayout() ) ) {
        request = repository.getURL() + artifact.nutFilePath();
        url = null;
      } else if ( "maven".equals( repository.getLayout() ) ) {
        // Example: "http://search.maven.org/remotecontent?filepath=org/testng/testng/6.8.7/testng-6.8.7.jar"
        request = repository.getURL() + artifact.mavenFilePath();
        url = new URL(request);
      } else {
        request = "";
        url = null;
      }
      log.debug( "Repository layout : '" + repository.getLayout() + "'" );
      log.debug( "Download request  : '" + request + "'" );

      // Now, trying to download the file
      try {
        fos = artifact.fileOutputStream();
        is = url.openStream();         // throws an IOException
        //read bytes from source file and write to destination file
        int total = 0;
        int nRead = 0;
        while((nRead = is.read(buffer)) != -1) {
          total += nRead;
          fos.write(buffer, 0, nRead);
        }
                                                                                                                                                        // Always close files.
        fos.flush();
        fos.close();
        is.close();
      } catch (MalformedURLException mue) {
            throw new Exception( "Wrong URL :" + mue );
      } catch(FileNotFoundException fnf) {
            throw new Exception( "Specified file not found :" + fnf );
      } catch(IOException ioe) {
            throw new Exception( "Error while writing file :" + ioe );
      }
    }

}
