package nut.goals;

import nut.goals.GoalException;
import nut.goals.packs.Jar;
import nut.goals.packs.War;
import nut.goals.packs.Zip;
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
        String targetDirectory      = basedir + File.separator + project.getTargetDirectory();
        String resourceDirectory    = basedir + File.separator + project.getResourceDirectory();
        String outputDirectory      = basedir + File.separator + project.getOutputDirectory();
        String webappDirectory      = basedir + File.separator + project.getWebappDirectory();

        String packaging            = project.getPackaging();
        String fullName             = targetDirectory + File.separator + project.getName() + "." + packaging;

        log.debug( "targetDirectory     = " + targetDirectory );
        log.debug( "resourceDirectory   = " + resourceDirectory );
        log.debug( "outputDirectory     = " + outputDirectory );
        log.debug( "webappDirectory     = " + webappDirectory );

        if (noop) {
            log.info( "NOOP: Packaging " + fullName );
            return;
        }
        log.info( "Packaging " + fullName );

        File targetDir = new File( targetDirectory );
        if ( !targetDir.exists() ) {
            targetDir.mkdirs();
        }

        if ( "jar".equals(packaging) ) {
          File outputDir = new File( outputDirectory );
          if ( !outputDir.exists() ) {
            throw new GoalException("\'" + outputDirectory + "\' is empty");
          }

          // Copy resource to output
          File resourceDir = new File( resourceDirectory );
          copyResource(resourceDir, outputDir);
          // Create jar
          Jar jar = new Jar(fullName);
          jar.archive(outputDirectory);
        } else if ( "war".equals(packaging) ) {
          // copy resourceDirectory files
          copyResource( new File( resourceDirectory ), new File( webappDirectory ) );
          // copy compiled files
          File outputDir = new File( basedir + File.separator + outputDirectory );
          copyResource( new File( outputDirectory ),   new File( webappDirectory + "/WEB-INF/classes" ) );
          // Create war
          War war = new War(fullName, project.getRepository(), project.getDependencies());
          war.archive(webappDirectory);
        } else if ( "zip".equals(packaging) ) {
          Zip zip = new Zip(fullName);
          zip.archive(resourceDirectory);
        }
    }

    // ==========================================================================
    private void copyResource(File source, File dest) throws GoalException {
        if ( !dest.exists() ) {
            dest.mkdirs();
        }
        if ( source.exists() ) {
          try {
            // Copy resource to output
            log.debug("* copy " + source.getPath() + " to " + dest.getPath());
            CopyOption options = StandardCopyOption.COPY_ATTRIBUTES;
            if (source.isDirectory())
                copyFolder(source, dest, options);
            else {
                ensureParentFolder(dest);
                copyFile(source, dest, options);
            }
          } catch ( IOException e) {
            throw new GoalException(e.getMessage());
          }
        } else {
          log.warn(source.getPath() + " not found");
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
