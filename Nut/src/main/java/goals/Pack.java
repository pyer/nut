package nut.goals;

import nut.goals.GoalException;
import nut.goals.packs.Jar;
import nut.goals.packs.Zip;
import nut.logging.Log;
import nut.model.Project;

import java.io.File;
import java.io.IOException;

import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

          File resourceDir = new File( basedir + File.separator + resourceDirectory );
          if ( resourceDir.exists() ) {
            try {
            // Copy resource to output
            copyResource(resourceDir, outputDir);
            } catch ( IOException e) {
              throw new GoalException(e.getMessage());
            }
          }

          Jar jar = new Jar(fullName);
          jar.archive(basedir + File.separator + outputDirectory);
        } else if ( "zip".equals(packaging) ) {
          Zip zip = new Zip(fullName);
          zip.archive(basedir + File.separator + resourceDirectory);
        }
    }

    // ==========================================================================
    private void copyResource(File source, File dest) throws IOException {
        log.debug("* copy " + source.getPath() + " to " + dest.getPath());
        CopyOption options = StandardCopyOption.COPY_ATTRIBUTES;
        if (source.isDirectory())
            copyFolder(source, dest, options);
        else {
            ensureParentFolder(dest);
            copyFile(source, dest, options);
        }
    }

    private void copyFolder(File source, File dest, CopyOption... options) throws IOException {
        if (!dest.exists())
            dest.mkdirs();
        File[] contents = source.listFiles();
        if (contents != null) {
            for (File f : contents) {
                File newFile = new File(dest.getCanonicalPath() + File.separator + f.getName());
                if (f.isDirectory())
                    copyFolder(f, newFile, options);
                else
                    copyFile(f, newFile, options);
            }
        }
    }

    private void copyFile(File source, File dest, CopyOption... options) throws IOException {
        Files.copy(source.toPath(), dest.toPath(), options);
    }

    private void ensureParentFolder(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists())
            parent.mkdirs();
    }

}
