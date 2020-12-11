package nut.build;

import nut.build.DependencyNotFoundException;
import nut.model.Dependency;
import nut.model.Project;

import nut.logging.Log;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.Iterator;

/**
 * Check if a dependency artifact file is present in the local repository.
 * if not, seeks artifact in central repositories
 */
public class DependencyChecker
{
    private Log log;

    // ----------------------------------------------------------------------
    public DependencyChecker()
    {
      log = new Log();
    }

    // ----------------------------------------------------------------------
    public void checkProject(Project project) throws DependencyNotFoundException
    {
      String repo = project.getRepository();
      for ( Iterator it = project.getDependencies().iterator(); it.hasNext(); ) {
          Dependency dep = (Dependency) it.next();
          log.debug("Check dependency " + dep.getPath() );
          if ( dep.isNotHere(repo) ) {
              try {
                download(dep, repo);
              } catch (SecurityException se) {
                throw new DependencyNotFoundException( "Dependency '" + dep.getPath() + "' is unreadable." );
              } catch (MalformedURLException mue) {
                throw new DependencyNotFoundException( "Dependency '" + dep.getPath() + "' has wrong URL :" + mue );
              } catch(FileNotFoundException fnf) {
                throw new DependencyNotFoundException( "Dependency '" + dep.getPath() + "' is not available :" + fnf );
              } catch(IOException ioe) {
                throw new DependencyNotFoundException( "Dependency '" + dep.getPath() + "' Error while writing file :" + ioe );
              }
              if ( dep.isNotHere(repo) ) {
                throw new DependencyNotFoundException("Dependency '" + dep.getPath() + "' is not found." );
              }
          }
      }
    }

    // ----------------------------------------------------------------------
    private void download(Dependency dep, String repo) throws MalformedURLException, FileNotFoundException, IOException
    {
      FileOutputStream fos;
      String outputFileName = repo + File.separator + dep.getPath();

      // Example: "http://search.maven.org/remotecontent?filepath=org/testng/testng/6.8.7/testng-6.8.7.jar"
      String request = "http://search.maven.org/remotecontent?" + dep.getMavenPath();
      URL url = new URL(request);
      log.debug( "Download [" + request + "] to " + outputFileName );

      // Use this for reading the data.
      InputStream is;
      int total = 0;
      int nRead = 0;
      byte[] buffer = new byte[8000];

      // Now, trying to download the file
      fos = new FileOutputStream( outputFileName );
      is = url.openStream();         // throws an IOException
      //read bytes from source file and write to destination file
      while((nRead = is.read(buffer)) != -1) {
          total += nRead;
          fos.write(buffer, 0, nRead);
      }
      // Always close files.
      fos.flush();
      fos.close();
      is.close();
    }

}
