package nut.goals;

import nut.artifact.Artifact;
import nut.goals.GoalException;
import nut.logging.Log;
import nut.model.Dependency;
import nut.project.Project;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PackWar
{
    /** Instance logger */
    private static Log log;

    // ==========================================================================
    public static void execute( Project project, Properties config )
        throws GoalException
    {
        String msg;
        log = new Log();
        Properties pp               = project.getModel().getProperties();
        String basedir              = (String)pp.getProperty( "basedir" );
        String targetDirectory      = project.getBuild().getTargetDirectory();
        String sourceDirectory      = project.getBuild().getSourceDirectory();
        String resourceDirectory    = project.getBuild().getResourceDirectory();
        String outputDirectory      = project.getBuild().getOutputDirectory();
        log.debug( "build.directory           = " + targetDirectory );
        log.debug( "build.sourceDirectory     = " + sourceDirectory );
        log.debug( "build.resourceDirectory   = " + resourceDirectory );
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
        // copy resourceDirectory files to resourceTargetDirectory
        String resourceTargetDirectory = config.getProperty("resourceTargetDirectory");
        if (resourceTargetDirectory != null) {
          copyResource(basedir + File.separator + resourceDirectory, basedir + File.separator + resourceTargetDirectory);
        }
        // copy dependencies files to libTargetDirectory
        String libTargetDirectory = config.getProperty("libTargetDirectory");
        if (libTargetDirectory != null) {
          //copyDependencies(project, basedir + File.separator + resourceDirectory, basedir + File.separator + libTargetDirectory);
          copyDependencies(project, File.separator + libTargetDirectory);
        }

        String archiveName = basedir + File.separator + targetDirectory + File.separator + artifactFileName;
        String dirName;

        dirName = basedir + File.separator + config.getProperty("webappDirectory", "main/webapp");
        log.debug( "configuration.webappDirectory    = " + dirName );
        archive( archiveName, dirName, "c" );

        dirName = basedir + File.separator + config.getProperty("classesDirectory", "target/classes");
        log.debug( "configuration.classesDirectory   = " + dirName );
        archive( archiveName, dirName, "u" );
    }

    // ==========================================================================
    private static void copyResource(String sourceDirectory, String targetDirectory)
        throws GoalException
    {
      log.debug("* copy " + sourceDirectory + " to " + targetDirectory);
      try {
        File targetDir = new File(targetDirectory);
        if(!targetDir.exists()){
          targetDir.mkdirs();
        }
        File sourceDir = new File(sourceDirectory);
        String[] paths = sourceDir.list();
        // for each name in the path array
        for(String path:paths) {
            // copy each file to output directory
            log.debug("** " + path);
            Path from = Paths.get(sourceDirectory + File.separator + path);
            Path to   = Paths.get(targetDirectory + File.separator + path);
            Files.copy(from, to);
        }
      } catch( Exception e ) {
        log.error( "Failed to copy resource file: " + e.getMessage(), e );
        throw new GoalException(e.getMessage());
      }
    }
    // ==========================================================================
    private static void copyDependencies(Project project, String targetDirectory)
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
    private static void archive(String finalName, String sourceDirectory, String mode)
        throws GoalException
    {
        String msg;
        File dir = new File(sourceDirectory);
        if( !dir.exists() ) {
          msg = "\'" + sourceDirectory + "\' is empty";
          log.error(msg);
          throw new GoalException(msg);
        }
        // ----------------------------------------------------------------------
        List<String> args = new ArrayList<String>();
        args.add( "jar" );
        args.add( mode + "f" );
        args.add( finalName );
        args.add( "-C" );
        args.add( sourceDirectory );
        args.add( "." );
        log.debug( "jar: -C " + sourceDirectory );
        // ----------------------------------------------------------------------
        // build the command line
        String[] command = (String[]) args.toArray( new String[ args.size() ] );
        // ----------------------------------------------------------------------
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
