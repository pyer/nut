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

import java.net.HttpURLConnection;
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
                File outputDir = new File( repo + File.separator + dep.getGroupPath() );
                if ( !outputDir.exists() ) {
                  outputDir.mkdirs();
                }
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
      String outputFileName = repo + File.separator + dep.getPath();
      // Example: "http://search.maven.org/remotecontent?filepath=org/testng/testng/6.8.7/testng-6.8.7.jar"
      String request = "http://search.maven.org/remotecontent?filepath=" + dep.getMavenPath();
      URL url = new URL(request);
      log.debug( "Download [" + request + "] to " + outputFileName );

      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setInstanceFollowRedirects( false );
      int responseCode = httpConn.getResponseCode();
      while (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
          log.debug("Response code  = " + responseCode);
          // get redirect url from "location" header field
          String newUrl = httpConn.getHeaderField("Location");
          // open the new connnection again
          httpConn = (HttpURLConnection) new URL(newUrl).openConnection();
          httpConn.setInstanceFollowRedirects( false );
          responseCode = httpConn.getResponseCode();
          log.debug("Redirect to " + newUrl);
      }

      if (responseCode == HttpURLConnection.HTTP_OK) {
          log.debug("Response code  = " + responseCode);
          log.debug("Content-Type   = " + httpConn.getContentType());
          log.debug("Content-Length = " + httpConn.getContentLength());
 
          // opens input stream from the HTTP connection
          InputStream inputStream = httpConn.getInputStream();
          // opens an output stream to save into file
          FileOutputStream outputStream = new FileOutputStream(outputFileName);
          int bytesRead = -1;
          byte[] buffer = new byte[4096];
          while ((bytesRead = inputStream.read(buffer, 0, 4096)) != -1) {
              outputStream.write(buffer, 0, bytesRead);
          }
 
          outputStream.close();
          inputStream.close();
      } else {
          throw new FileNotFoundException("Server replied HTTP code: " + responseCode);
      }
      httpConn.disconnect();
    }
}
