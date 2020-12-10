package nut.goals;

import nut.goals.GoalException;
import nut.logging.Log;
import nut.model.Project;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class Pack implements Goal
{
    /** Instance logger */
    private Log log;

    // ==========================================================================
    public void execute(Project project, boolean noop) throws GoalException
    {
        String msg;
        log = new Log();
        String basedir              = project.getBaseDirectory();
        String targetDirectory      = project.getTargetDirectory();
        String sourceDirectory      = project.getSourceDirectory();
        String resourceDirectory    = project.getResourceDirectory();
        String outputDirectory      = project.getOutputDirectory();

        log.debug( "build.directory           = " + targetDirectory );
        log.debug( "build.sourceDirectory     = " + sourceDirectory );
        log.debug( "build.resourceDirectory   = " + resourceDirectory );
        log.debug( "build.outputDirectory     = " + outputDirectory );

        String artifactId           = project.getName();
        String version              = project.getVersion();
        String packaging            = project.getPackaging();
        String artifactFileName     = artifactId + "." + packaging;

        log.debug( "project.artifactId        = " + artifactId );
        log.debug( "project.version           = " + version );
        log.debug( "project.packaging         = " + packaging );

        log.info( "Packaging \'" + artifactFileName + "\'" );


        if ( artifactId==null || (artifactId.trim().isEmpty() ) ) {
            msg = "\'project.artifactId\' property is undefined";
            log.error(msg);
            throw new GoalException(msg);
        }

        File targetDir = new File( basedir + File.separator + targetDirectory );
        if ( !targetDir.exists() ) {
            targetDir.mkdirs();
        }

        File outputDir = new File( basedir + File.separator + outputDirectory );
        if ( !outputDir.exists() ) {
            msg = "\'" + outputDirectory + "\' is empty";
            log.error(msg);
            throw new GoalException(msg);
        }
        archive( artifactFileName, basedir + File.separator + targetDirectory, basedir + File.separator + outputDirectory, "c" );
    }

/*
   // targetDirectory and resourceDirectory are full path names
    private void zip(String finalName, String targetDirectory, String resourceDirectory)
        throws GoalException
    {
        try {
          FileOutputStream dest = new FileOutputStream( targetDirectory + File.separator + finalName );
          ZipOutputStream  out  = new ZipOutputStream( new BufferedOutputStream(dest));
*/


    // ==========================================================================
    private void archive(String finalName, String targetDirectory, String outputDirectory, String mode)
        throws GoalException
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
            if ( status != 0 ) {
                throw new GoalException("Error in child process");
            }
        } catch ( InterruptedException e ) {
            log.error( "Failed to archive: " + e.getMessage(), e );
            throw new GoalException(e.getMessage());
        } catch ( IOException e ) {
            log.error( "Failed to archive: " + e.getMessage(), e );
            throw new GoalException(e.getMessage());
        }
        // ----------------------------------------------------------------------
    }
}
