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

        Properties pluginProperties = project.getModel().getProperties();
        String basedir              = (String)pluginProperties.getProperty( "basedir" );
        log.info( "   Release \'" + basedir + "/nut.xml\'" );
        incrementVersion( new File( basedir + "/nut.xml" ) );
/*
        String repository           = (String)pluginProperties.getProperty( "repository" );
        String buildDirectory       = (String)pluginProperties.getProperty( "build.directory" );
        String groupId              = (String)pluginProperties.getProperty( "project.groupId" );
        String artifactId           = (String)pluginProperties.getProperty( "project.artifactId" );
        String version              = (String)pluginProperties.getProperty( "project.version" );
        String packaging            = (String)pluginProperties.getProperty( "project.packaging" );

        log.debug( "repository                = " + repository );
        log.debug( "build.directory           = " + buildDirectory );
        log.debug( "project.artifactId        = " + artifactId );
        log.debug( "project.version           = " + version );
        log.debug( "project.packaging         = " + packaging );

            log.info( "   Installing \'" + artifactId + "\'" );
            // + "-" + version + "." + packaging
            String group = groupId.replace( '.', File.separatorChar );
            String artifactName = repository + File.separator + group + File.separator + artifactId + "-" + version + "." + packaging;
            if( !packaging.equals("modules") )
            {
                //install: copy target file to local repository
                String buildName = basedir + File.separator + buildDirectory + File.separator + artifactId + "." + packaging;
                copyFile( buildName, artifactName, version );
            }
            //install: copy nut.xml file to local repository
            String nutName = basedir + File.separator + "nut.xml";
            copyFile( nutName, artifactName + ".nut", version );
*/
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
      return version;
    }

    public static String getNutVersion0( File nut )
        throws Exception
    {
        String version = "";
        String line = null;
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
        return version;
    }

}
