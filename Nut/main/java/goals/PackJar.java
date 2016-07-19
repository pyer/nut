package nut.goals;

import nut.logging.Log;
import nut.project.Project;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PackJar
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
        String targetDirectory      = project.getBuild().getTargetDirectory();
        String sourceDirectory      = project.getBuild().getSourceDirectory();
        String resourceDirectory    = project.getBuild().getResourceDirectory();
        String outputDirectory      = project.getBuild().getOutputDirectory();

        log.debug( "build.directory           = " + targetDirectory );
        log.debug( "build.sourceDirectory     = " + sourceDirectory );
        log.debug( "build.resourceDirectory   = " + resourceDirectory );
        log.debug( "build.outputDirectory     = " + outputDirectory );

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

        File outputDir = new File( basedir + File.separator + outputDirectory );
        if ( !outputDir.exists() ) {
            log.error( "\'" + outputDirectory + "\' is empty" );
            throw new Exception();
        }
        archive( artifactFileName, basedir + File.separator + targetDirectory, basedir + File.separator + outputDirectory, "c" );
    }

    // ==========================================================================
    private static void archive(String finalName, String targetDirectory, String outputDirectory, String mode)
        throws Exception
    {
        // ----------------------------------------------------------------------
        List<String> args = new ArrayList<String>();
        args.add( "jar" );
        args.add( mode + "f" );
        args.add( targetDirectory + File.separator + finalName );
        args.add( "-C" );
        args.add( outputDirectory );
        args.add( "." );
        log.debug( "jar: -C " + outputDirectory );
        // ----------------------------------------------------------------------
        // build the command line
        String[] command = (String[]) args.toArray( new String[ args.size() ] );
        try {
            Process child = Runtime.getRuntime().exec(command);
            int status = child.waitFor();
            if ( status != 0 )
            {
                throw new Exception();
            }
        } catch ( IOException e ) {
            log.error( "Failed to archive. Reason: " + e.getMessage(), e );
            throw new Exception();
        }
        // ----------------------------------------------------------------------
    }
}
