package nut.goals;

import nut.Logger;
import nut.goals.GoalException;
import nut.goals.packs.Jar;
import nut.goals.packs.War;
import nut.goals.packs.Zip;
import nut.model.Project;

import java.io.File;

public class Pack implements Goal
{

    public void execute(Project project) throws GoalException
    {
        Logger log = new Logger();
        String packaging = project.getPackaging();
        String fullName  = project.getBaseDirectory() + File.separator
                         + project.getTargetDirectory() + File.separator
                         + project.getName() + "." + packaging;

        log.info( "Packaging" );
        log.debug( fullName );
        if (project.noop()) {
            return;
        }

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
