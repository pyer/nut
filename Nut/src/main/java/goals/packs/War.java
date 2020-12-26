package nut.goals.packs;

import nut.goals.GoalException;
import nut.goals.packs.Zip;
import nut.logging.Log;
import nut.model.Dependency;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private String repository;
    private List<Dependency> dependencies;

    // ==========================================================================
    public War( String name, String repository, List<Dependency> dependencies )
    {
        this.name = name;
        this.repository = repository;
        this.dependencies = dependencies;
        log = new Log();
    }

    // ==========================================================================
    public void archive( String directory ) throws GoalException
    {
        // ----------------------------------------------------------------------
        String libDirectory = directory + "/WEB-INF/lib";
        File libDir = new File( libDirectory );
        if ( !libDir.exists() ) {
            libDir.mkdirs();
        }

        try {
            log.debug("* copy dependencies to " + libDirectory);
            for ( Iterator it = dependencies.iterator(); it.hasNext(); ) {
              Dependency dep = (Dependency) it.next();
              String depPath = repository + dep.getPath();
              log.debug("** " + depPath);
              Path dest = Paths.get(libDirectory + "/" + dep.getName());
              Files.copy(Paths.get(depPath), dest);
            }
        } catch( Exception e ) {
            log.error( "Failed to copy dependency: " + e.getMessage(), e );
            throw new GoalException(e.getMessage());
        }
        Zip zip = new Zip(this.name);
        zip.archive(directory);
    }

    // ==========================================================================
}
