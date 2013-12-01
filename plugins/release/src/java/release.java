package nut.plugins;

import nut.logging.Log;
import nut.project.NutProject;

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

/* Usage:
 * nut release:1.2               => increments release version
 * nut release:1.2 -Drelease=2.0 => sets version 2.0 in nut.xml
 *
 */

public class release
{
    /** Instance logger */
    private static Log log;

    public static void execute( NutProject project, Log logger )
        throws Exception
    {
        log = logger;

        Properties pp     = project.getModel().getProperties();
        String basedir    = (String)pp.getProperty( "basedir" );
        String release    = (String)pp.getProperty( "release" );
        log.info( "   Release \'" + basedir + "/nut.xml\'" );
        if( release == null ) {
            incrementVersion( new File( basedir + "/nut.xml" ) );
        } else {
            setNutVersion( new File( basedir + "/nut.xml" ), release );
        }
    }

    public static void incrementVersion( File nut )
    {
       String version = getNutVersion( nut );
       int i = version.indexOf("-SNAPSHOT");
       if( i > 0 ) {
         setNutVersion( nut, version.substring(0, i) );
       } else {
         i = version.indexOf(".");
         if( i > 0 ) {
           try {
             int major = Integer.parseInt( version.substring( 0, i ) ); 
             int minor = Integer.parseInt( version.substring( i+1 ) ); 
             version = String.valueOf(major) + "." + String.valueOf(minor+1) + "-SNAPSHOT";
             setNutVersion( nut, version );
           }
           catch( NumberFormatException e ) {
             System.out.print("Invalid version: "+version );
             version = null;
           }
         } else {
             System.out.print("Invalid version: "+version );
         }
       }
    }

    public static void setReleaseVersion( File nut )
    {
       String version = getNutVersion( nut );
       int i = version.indexOf("-SNAPSHOT");
       if( i > 0 ) {
         setNutVersion( nut, version.substring(0, i) );
       }
    }

    public static void setSnapshotVersion( File nut )
    {
       String version = getNutVersion( nut );
       if( !version.endsWith("-SNAPSHOT") ) {
         setNutVersion( nut, version + "-SNAPSHOT" );
       }
    }

    public static void setNutVersion( File nut, String version )
    {
      String line = null;
      List<String> buffer = new ArrayList<String>();

      log.debug( "setNutVersion: " + version);
      try {
        // FileReader reads text files in the default encoding.
        FileReader fr = new FileReader(nut);
        // Always wrap FileReader in BufferedReader.
        BufferedReader br = new BufferedReader(fr);
        while ((line = br.readLine()) != null)
        {
          buffer.add(line);
        }
        br.close();			
      }
      catch(FileNotFoundException e) {
        //return(version);
      }
      catch(IOException e) {
        //return(version);
      }

      try {
        FileWriter fw = new FileWriter(nut); 
        // Always wrap FileWriter in BufferedWriter.
        BufferedWriter bw = new BufferedWriter(fw);
        // Note that write() does not automatically
        // append a newline character.
        int i = 0;
        int n = buffer.size();
        int b = -1;
        int e = -1;
        while ( (b == -1) && (e == -1) && (i < n) ) {
             line = buffer.get(i);
             b = line.indexOf("<version>");
             e = line.indexOf("</version>");
             if( b>=0 && e>10 ) {
               String s = line.substring(0,b+9);
               line = s + version + "</version>";
               log.debug( "setNutVersion: " + version + " done");
             }
             bw.write(line+"\n");
             i++;
        }
        while ( i < n ) {
          line = buffer.get(i);
          bw.write(line+"\n");
          i++;
        }
        bw.close();
      }
      catch(Exception e)
      {
        //return(version);
      }

    }

    public static String getNutVersion( File nut )
    {
      String version = "";
      String line = null;

      try {
        // FileReader reads text files in the default encoding.
        FileReader fr = new FileReader(nut);
        // Always wrap FileReader in BufferedReader.
        BufferedReader br = new BufferedReader(fr);
        int b = -1;
        int e = -1;
        while ((line = br.readLine()) != null && (b == -1) && (e == -1 ) )
        {
          b = line.indexOf("<version>");
          e = line.indexOf("</version>");
          if( b>=0 && e>10 )
          {
            version=line.substring(b+9,e);
  //  System.out.print(line);
          }
        }
        // Always close files.
        br.close();			
      }
      catch(FileNotFoundException e) {
        return(version);
      }
      catch(IOException e) {
        return(version);
      }
      log.debug( "getNutVersion: " + version );
      return version;
    }

}
