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
        log.info( "   Release \'" + basedir + "/nut.xml\'" );
        incrementVersion( new File( basedir + "/nut.xml" ) );
    }

    public static void incrementVersion( File nut )
    {
       String version = getNutVersion( nut );
       log.info("     from "+version );
       int i = version.indexOf("-SNAPSHOT");
       log.debug("i="+String.valueOf(i));
       if( i > 0 ) {
         version = version.substring(0, i);
         setNutVersion( nut, version );
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
       log.info("     to   "+version );
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

      log.debug("setVersion: "+version );
      try {
        // FileReader reads text files in the default encoding.
        FileReader fr = new FileReader(nut);
        // Always wrap FileReader in BufferedReader.
        BufferedReader br = new BufferedReader(fr);
        while ((line = br.readLine()) != null)
        {
          buffer.add(line);
          log.debug("READ: "+line );
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
             }
             bw.write(line+"\n");
             log.debug("WRITE: "+line );
             i++;
        }
        while ( i < n ) {
          line = buffer.get(i);
          bw.write(line+"\n");
          log.debug("WRITE: "+line );
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
      log.debug("getVersion: "+version );
      return version;
    }

}
