package nut.goals;

import nut.artifact.Artifact;
import nut.build.archive.Archiver;
import nut.build.archive.ArchiverException;
import nut.goals.GoalException;
import nut.logging.Log;
import nut.model.Dependency;
import nut.model.Project;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PackWar implements Goal
{
    /** Instance logger */
    private Log log;

/*
<configuration>
  <classesDirectory>target/classes</classesDirectory>
  <webappDirectory>main/webapp</webappDirectory>
  <resourceTargetDirectory>target/classes/WEB-INF/classes</resourceTargetDirectory>
  <libTargetDirectory>target/classes/WEB-INF/lib</libTargetDirectory>
</configuration>

<webappDirectory>main/webapp</webappDirectory>
<libOutputDirectory>target/classes/WEB-INF/lib</libOutputDirectory>

*/
    // ==========================================================================
    public void execute( Project project ) throws GoalException
    {
        String msg;
        log = new Log();
        Properties pp               = project.getProperties();
        String basedir              = (String)pp.getProperty( "basedir" ) + File.separator;
        String targetDirectory      = project.getLayout().getTargetDirectory();
        String sourceDirectory      = project.getLayout().getSourceDirectory();
        String resourceDirectory    = project.getLayout().getResourceDirectory();
        String webappDirectory      = project.getLayout().getWebappDirectory();
        String outputDirectory      = project.getLayout().getOutputDirectory();

        log.debug( "build.directory           = " + targetDirectory );
        log.debug( "build.sourceDirectory     = " + sourceDirectory );
        log.debug( "build.resourceDirectory   = " + resourceDirectory );
        log.debug( "build.webappDirectory     = " + webappDirectory );
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
            msg = "\'project.artifactId\' property is undefined";
            log.error(msg);
            throw new GoalException(msg);
        }

        try {
          // copy resourceDirectory files
          copyResources(basedir + resourceDirectory, basedir + targetDirectory + "/webapp/WEB-INF/classes");
          // copy compiled files
          copyResources(basedir + outputDirectory,   basedir + targetDirectory + "/webapp/WEB-INF/classes");
          // copy dependencies files
          copyDependencies(project, basedir + targetDirectory + "/webapp/WEB-INF/lib");
        } catch(IOException e) {
          throw new GoalException(e.getMessage());
        }

        try {
          String archiveName = basedir + File.separator + targetDirectory + File.separator + artifactFileName;
          Archiver zip = new Archiver();
          zip.setDestFile( new File(archiveName) );
          zip.create();
          zip.addDirectory(basedir + webappDirectory);
          zip.addDirectory(basedir + targetDirectory + File.separator + "webapp");
          zip.close();
        } catch(ArchiverException e) {
          throw new GoalException(e.getMessage());
        }
    }

    // ==========================================================================
    private void copyResources(String sourceDirectory, String targetDirectory)
      throws IOException {
        log.debug("* copy " + sourceDirectory + " to " + targetDirectory);
        File source = new File(sourceDirectory);
        File dest   = new File(targetDirectory);
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
                File newFile = new File(dest.getAbsolutePath() + File.separator + f.getName());
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

    // ==========================================================================
    private void copyDependencies(Project project, String targetDirectory)
        throws GoalException
    {
      log.debug("* copy dependencies to " + targetDirectory);
      try {
        File targetDir = new File(targetDirectory);
        if(!targetDir.exists()){
          targetDir.mkdirs();
        }
        for ( Iterator it = project.getDependencies().iterator(); it.hasNext(); ) {
          Dependency dep = (Dependency) it.next();
          if( dep.getScope().equals("runtime") || dep.getScope().equals("compile") ) {
            Artifact artifactDep = new Artifact( dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getType() );
            String path = artifactDep.getPath();
            log.debug("** " + path);
            Path from = Paths.get(path);
            Path to   = Paths.get(targetDirectory + File.separator + artifactDep.nutFileName());
            Files.copy(from, to);
          }
        }
      } catch( Exception e ) {
        log.error( "Failed to copy resource file: " + e.getMessage(), e );
        throw new GoalException(e.getMessage());
      }
    }

    // ==========================================================================
}
