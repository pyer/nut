package nut.goals;

import nut.goals.GoalException;
import nut.goals.packs.Jar;
import nut.goals.packs.Zip;
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
        log = new Log();
        String basedir              = project.getBaseDirectory();
        String targetDirectory      = project.getTargetDirectory();
        String resourceDirectory    = project.getResourceDirectory();
        String outputDirectory      = project.getOutputDirectory();

        log.debug( "build.directory           = " + targetDirectory );
        log.debug( "build.resourceDirectory   = " + resourceDirectory );
        log.debug( "build.outputDirectory     = " + outputDirectory );

        String packaging            = project.getPackaging();
        String fullName             = basedir + File.separator + targetDirectory + File.separator + project.getName() + "." + packaging;

        if (noop) {
            log.info( "NOOP:  Packaging " + fullName );
            return;
        }
        log.info( "Packaging " + fullName );

        File targetDir = new File( basedir + File.separator + targetDirectory );
        if ( !targetDir.exists() ) {
            targetDir.mkdirs();
        }

        if ( "jar".equals(packaging) ) {
          File outputDir = new File( basedir + File.separator + outputDirectory );
          if ( !outputDir.exists() ) {
            throw new GoalException("\'" + outputDirectory + "\' is empty");
          }
          Jar jar = new Jar(fullName);
          jar.archive(basedir + File.separator + outputDirectory);
        } else if ( "zip".equals(packaging) ) {
          Zip zip = new Zip(fullName);
          zip.archive(basedir + File.separator + resourceDirectory);
        }
    }
}
