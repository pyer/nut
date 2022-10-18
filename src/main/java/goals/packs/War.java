package nut.goals.packs;

import nut.goals.GoalException;
import nut.goals.packs.Zip;
import nut.goals.packs.util.CopyFiles;
import nut.goals.packs.util.ZipFiles;
import nut.logging.Log;
import nut.model.Dependency;
import nut.model.Project;

import java.io.File;
import java.io.IOException;

import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class War
{
    /** Instance logger */
    private Log log;
    private String name;

    // ==========================================================================
    public War( String name )
    {
        this.name = name;
        log = new Log();
    }

    // ==========================================================================
    public void archive(Project project) throws GoalException
    {
        String basedir              = project.getBaseDirectory();
        String resourceDirectory    = basedir + File.separator + project.getResourceDirectory();
        String outputDirectory      = basedir + File.separator + project.getOutputDirectory();
        String webappDirectory      = basedir + File.separator + project.getWebappDirectory();

        log.debug( "resourceDirectory   = " + resourceDirectory );
        log.debug( "outputDirectory     = " + outputDirectory );
        log.debug( "webappDirectory     = " + webappDirectory );

        // ----------------------------------------------------------------------
        // copy resource files
        try {
            CopyFiles resource = new CopyFiles( resourceDirectory, webappDirectory );
            resource.process();
        } catch( Exception e ) {
            log.error( "Failed to copy resource: " + e.getMessage(), e );
            throw new GoalException(e.getMessage());
        }
        // copy compiled files
        try {
            CopyFiles compiled = new CopyFiles( outputDirectory,   webappDirectory + "/WEB-INF/classes" );
            compiled.process();
        } catch( Exception e ) {
            log.error( "Failed to copy compiled source: " + e.getMessage(), e );
            throw new GoalException(e.getMessage());
        }
        // ----------------------------------------------------------------------
        // copy dependencies
        String libDirectory = webappDirectory + "/WEB-INF/lib";
        File libDir = new File( libDirectory );
        if ( !libDir.exists() ) {
            libDir.mkdirs();
        }
        String repository = project.getRepository();
        try {
            log.debug("* copy dependencies to " + libDirectory);
            for ( Dependency dep : project.getDependencies() ) {
              String depPath = repository + dep.getPath();
              log.debug("** " + depPath);
              Path dest = Paths.get(libDirectory + "/" + dep.getName());
              if ( !dest.toFile().exists() )
                  Files.copy(Paths.get(depPath), dest);
            }
        } catch( Exception e ) {
            log.error( "Failed to copy dependency: " + e.getMessage(), e );
            throw new GoalException(e.getMessage());
        }
        // ----------------------------------------------------------------------
        // Create war file
        try {
            ZipFiles zip = new ZipFiles( webappDirectory, name );
            zip.process();
        } catch( Exception e ) {
            log.error( "Failed to zip: " + e.getMessage(), e );
            throw new GoalException(e.getMessage());
        }
    }

    // ==========================================================================
}
