package nut.build;

import nut.Logger;
import nut.build.DependencyNotFoundException;
import nut.model.Dependency;
import nut.model.Project;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Check if a dependency artifact file is present in the local repository.
 * if not, seeks artifact in central repository
 */
public class DependencyChecker
{
    private Logger log;

    // ----------------------------------------------------------------------
    public DependencyChecker()
    {
      log = new Logger();
    }

    // ----------------------------------------------------------------------
    public void checkProject(Project project) throws DependencyNotFoundException
    {
      String repo = project.getRepository();
      String remote = project.getRemoteRepository();
      for ( Dependency dep : project.getDependencies() ) {
          log.debug("Check dependency " + dep.getPath() );
          if ( dep.isNotHere(repo) ) {
            if ( dep.snapshotIsNotHere(repo) ) {
              try {
                File outputDir = new File( repo + dep.getGroup() );
                if ( !outputDir.exists() ) {
                  outputDir.mkdirs();
                }
                download(dep, repo, remote);
              } catch (SecurityException se) {
                throw new DependencyNotFoundException( "Dependency '" + dep.getPath() + "' is unreadable." );
              } catch (URISyntaxException use) {
                throw new DependencyNotFoundException( "Dependency '" + dep.getPath() + "' has an URI syntax error :" + use );
              } catch (MalformedURLException mue) {
                throw new DependencyNotFoundException( "Dependency '" + dep.getPath() + "' has a wrong URL :" + mue );
              } catch(FileNotFoundException fnf) {
                throw new DependencyNotFoundException( "Dependency '" + dep.getPath() + "' is not available :" + fnf );
              } catch(IOException ioe) {
                throw new DependencyNotFoundException( "Dependency '" + dep.getPath() + "' fails to writing file :" + ioe );
              }
              if ( dep.isNotHere(repo) ) {
                throw new DependencyNotFoundException("Dependency '" + dep.getPath() + "' is not found." );
              }
            }
          }
      }
    }

    // ----------------------------------------------------------------------
    private void download(Dependency dep, String repo, String remote) throws URISyntaxException, MalformedURLException, FileNotFoundException, IOException
    {
      String outputFileName = repo + dep.getPath();
      // Example: "http://search.maven.org/remotecontent?filepath=org/testng/testng/6.8.7/testng-6.8.7.jar"
      String request = remote + dep.getPath().substring(1);
      URL url = new URI(request).toURL();
      log.info( "Download [" + request + "] to " + outputFileName );

      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setInstanceFollowRedirects( false );
      log.debug("Connect to " + remote);
      int responseCode = 0;
      try {
        int retries = 12;
        responseCode = httpConn.getResponseCode();
        while (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
          log.debug("Response code  = " + responseCode);
          // get redirect url from "location" header field
          String newUrl = httpConn.getHeaderField("Location");
          // open the new connnection again
          httpConn = (HttpURLConnection) new URI(newUrl).toURL().openConnection();
          httpConn.setInstanceFollowRedirects( false );
          responseCode = httpConn.getResponseCode();
          log.debug("Redirect to " + newUrl);
          retries--;
          if (retries<0) {
              throw new FileNotFoundException("Server " + newUrl + " is not responding.");
          }
        }
      } catch(Exception e) {
          throw new FileNotFoundException("Server " + remote + " is not responding.");
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
