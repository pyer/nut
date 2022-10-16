package nut.goals;

import nut.goals.GoalException;
import nut.goals.packs.Jar;
import nut.goals.packs.War;
import nut.goals.packs.Zip;
import nut.logging.Log;
import nut.model.Project;

import java.io.File;
/*
import java.io.IOException;

import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
*/

public class Pack implements Goal
{
    /** Instance logger */
    private Log log;

    public void execute(Project project) throws GoalException
    {
        log = new Log();
        String packaging = project.getPackaging();
        String fullName  = project.getBaseDirectory() + File.separator
                         + project.getTargetDirectory() + File.separator
                         + project.getName() + "." + packaging;

        if (project.noop()) {
            log.info( "NOOP: Packaging " + fullName );
            return;
        }
        log.info( "Packaging " + fullName );

        // Create target directory if it is needed
        File targetDir = new File( project.getBaseDirectory() + File.separator + project.getTargetDirectory() );
        if ( !targetDir.exists() ) {
            targetDir.mkdirs();
        }

        // Select archiver from packaging
        if ( "jar".equals(packaging) ) {
          Jar jar = new Jar(fullName);
          jar.archive(project);
        } else if ( "war".equals(packaging) ) {
          War war = new War(fullName);
          war.archive(project);
        } else if ( "zip".equals(packaging) ) {
          Zip zip = new Zip(fullName);
          zip.archive(project);
        }
    }

}
