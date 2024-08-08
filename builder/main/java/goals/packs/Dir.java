package nut.goals.packs;

import nut.Logger;
import nut.goals.GoalException;
import nut.goals.packs.util.CopyFiles;
import nut.model.Dependency;
import nut.model.Project;

import java.io.File;
import java.io.IOException;

import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Dir
{
    /** Instance logger */
    private Logger log;
    private String name;

    // ==========================================================================
    public Dir( String name )
    {
        this.name = name;
        log = new Logger();
    }

    // ==========================================================================
    public void archive(Project project) throws GoalException
    {
        String basedir              = project.getBaseDirectory();
        String resourceDirectory    = basedir + File.separator + project.getResourceDirectory();
        String webappDirectory      = basedir + File.separator + project.getWebappDirectory();
        String outputDirectory      = basedir + File.separator + project.getOutputDirectory();
        String targetDirectory      = basedir + File.separator + project.getTargetDirectory();

        log.debug( "resourceDirectory   = " + resourceDirectory );
        log.debug( "outputDirectory     = " + outputDirectory );
        log.debug( "targetDirectory     = " + targetDirectory );

        // ----------------------------------------------------------------------
        // copy resource files
        try {
            CopyFiles resource = new CopyFiles( resourceDirectory, targetDirectory );
            resource.process();
        } catch( Exception e ) {
            log.error( "Failed to copy resource: " + e.getMessage(), e );
            throw new GoalException(e.getMessage());
        }
  /*
        // ----------------------------------------------------------------------
        // copy compiled files
        try {
            CopyFiles compiled = new CopyFiles( outputDirectory,   targetDirectory );
            compiled.process();
        } catch( Exception e ) {
            log.error( "Failed to copy compiled source: " + e.getMessage(), e );
            throw new GoalException(e.getMessage());
        }
        // ----------------------------------------------------------------------
        // copy dependencies
        String repository = project.getRepository();
        try {
            log.debug("* copy dependencies to " + name);
            for ( Dependency dep : project.getDependencies() ) {
              String depPath = repository + dep.getPath();
              log.debug("** " + depPath);
              Path dest = Paths.get(name + dep.getLibName());
              if ( !dest.toFile().exists() )
                  Files.copy(Paths.get(depPath), dest);
            }
        } catch( Exception e ) {
            log.error( "Failed to copy dependency: " + e.getMessage(), e );
            throw new GoalException(e.getMessage());
        }
*/
        // ----------------------------------------------------------------------
    }

    // ==========================================================================
}
