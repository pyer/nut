package nut.goals;

import nut.logging.Log;
import nut.project.NutProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import java.lang.InterruptedException;
import java.lang.Process;
import java.lang.Runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
[DEBUG] Configuring mojo 'org.apache.maven.plugins:maven-jar-plugin:2.2:jar' --
[DEBUG]   (f) classesDirectory = C:\ab\plugins\clean\target\classes
[DEBUG]   (f) defaultManifestFile = C:\ab\plugins\clean\target\classes\META-INF\MANIFEST.MF
[DEBUG]   (f) finalName = clean-1.0
[DEBUG]   (f) forceCreation = false
[DEBUG]   (f) outputDirectory = C:\ab\plugins\clean\target
[DEBUG]   (f) project = MavenProject: ab.plugins:clean:1.0 @ C:\ab\plugins\clean\pom.xml
[DEBUG]   (f) useDefaultManifestFile = false
[DEBUG] -- end configuration --
[INFO] [jar:jar]
[DEBUG] isUp2date: false (Input file C:\ab\plugins\clean\target\classes\ab\plugins\clean.class is newer.)
[INFO] Building jar: C:\ab\plugins\clean\target\clean-1.0.jar
[DEBUG] adding directory META-INF/
[DEBUG] adding entry META-INF/MANIFEST.MF
[DEBUG] adding directory ab/
[DEBUG] adding directory ab/plugins/
[DEBUG] adding entry ab/plugins/clean.class
[DEBUG] adding directory META-INF/maven/
[DEBUG] adding directory META-INF/maven/ab.plugins/
[DEBUG] adding directory META-INF/maven/ab.plugins/clean/
[DEBUG] adding entry META-INF/maven/ab.plugins/clean/pom.xml
[DEBUG] adding entry META-INF/maven/ab.plugins/clean/pom.properties
 *
 */
public class pack
{
    /** Instance logger */
    private static Log log;

    public static void execute( NutProject project, Properties config )
        throws Exception
    {
        log = new Log();
        Properties pp               = project.getModel().getProperties();
        String basedir              = (String)pp.getProperty( "basedir" );
        String repository           = (String)pp.getProperty( "nut.home" );
        String targetDirectory      = project.getBuild().getTargetDirectory();
        String sourceDirectory      = project.getBuild().getSourceDirectory();
//        String testSourceDirectory  = project.getBuild().getTestSourceDirectory();
        String outputDirectory      = project.getBuild().getOutputDirectory();
//        String testOutputDirectory  = project.getBuild().getTestOutputDirectory();

        log.debug( "build.directory           = " + targetDirectory );
        log.debug( "build.sourceDirectory     = " + sourceDirectory );
//        log.debug( "build.testSourceDirectory = " + testSourceDirectory );
        log.debug( "build.outputDirectory     = " + outputDirectory );
//        log.debug( "build.testOutputDirectory = " + testOutputDirectory );

        String artifactId           = project.getArtifactId();
        String version              = project.getVersion();
        String packaging            = project.getPackaging();
        String artifactFile         = artifactId + "." + packaging;

        log.debug( "project.artifactId        = " + artifactId );
        log.debug( "project.version           = " + version );
        log.debug( "project.packaging         = " + packaging );
        
        log.info( "   Packaging \'" + artifactFile + "\'" );

        if ( artifactId==null || (artifactId.trim().length() == 0 ) )
        {
            log.error( "\'project.artifactId\' property is undefined" );
            throw new Exception();
        }
        
        File outputDir = new File( basedir + File.separator + outputDirectory );
        if ( !outputDir.exists() )
        {
            log.error( "\'" + outputDirectory + "\' is empty" );
            throw new Exception();
        }

        File targetDir = new File( basedir + File.separator + targetDirectory );
        if ( !targetDir.exists() )
        {
            targetDir.mkdirs();
        }

        if( packaging.equals( "war" ) )
        {
            archive( artifactFile, basedir + File.separator + targetDirectory, basedir + File.separator + sourceDirectory, "c" );
            archive( artifactFile, basedir + File.separator + targetDirectory, basedir + File.separator + outputDirectory, "u" );
        }
        else
        {
            archive( artifactFile, basedir + File.separator + targetDirectory, basedir + File.separator + outputDirectory, "c" );
        }
        //archive( artifactId + ".src.jar", basedir + File.separator + sourceDirectory, basedir + File.separator + outputDirectory );
    }

    private static void archive(String finalName, String targetDirectory, String outputDirectory, String mode)
        throws Exception
    {
        // ----------------------------------------------------------------------
        List<String> args = new ArrayList<String>();
        args.add( "jar" );
        args.add( mode + "f" );
        args.add( targetDirectory + File.separator + finalName );
        args.add( "-C" );
        args.add( outputDirectory + File.separator );
        args.add( "." );
        log.debug( "jar: -C " + outputDirectory );
        // ----------------------------------------------------------------------
        // build the command line
        String[] command = (String[]) args.toArray( new String[ args.size() ] );
        try
        {
            Process child = Runtime.getRuntime().exec(command);
            int status = child.waitFor();
            if ( status != 0 )
            {
                throw new Exception();
            }
        }
        catch ( IOException e )
        {
            log.error( "Failed to archive. Reason: " + e.getMessage(), e );
            throw new Exception();
        }
        // ----------------------------------------------------------------------
    }

}
