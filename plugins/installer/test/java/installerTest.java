package nut.plugins;

import nut.logging.Log;

import nut.model.Model;
import nut.project.NutProject;

import junit.framework.TestCase;

import java.io.File;
import java.io.Reader;
import java.util.Properties;


public class installerTest
    extends TestCase
{
    /** Instance logger */
    private static Log log;

    private final String LOCAL_REPO = "target/repository";
    private NutProject project;
    
    public void setUp()
        throws Exception
    {
        super.setUp();
        Model model = new Model();
        model.addProperty( "basedir",    "target" );
        model.addProperty( "repository", LOCAL_REPO );
        model.addProperty( "build.directory",    "build" );
        model.addProperty( "project.groupId",    "local.group" );
        model.addProperty( "project.artifactId", "artifact" );
        model.addProperty( "project.version",    "0.0-SNAPSHOT" );
        model.addProperty( "project.packaging",  "file" );
        project = new NutProject(model);
        
        //System.out.println( ">>>Cleaning " + LOCAL_REPO );
        new File( LOCAL_REPO ).delete();
        log = new Log();
    }

    public void testRepositoryIsDeleted()
    {
        File repo = new File( LOCAL_REPO );
        //System.out.println( repo.getAbsolutePath() );
        assertFalse ( repo.exists() );
    }

    public void testRepository()
    {
        File repo = new File( LOCAL_REPO );
        repo.mkdir();
        //System.out.println( repo.getAbsolutePath() );
        assertTrue ( repo.exists() );
    }

    public void testBasicInstallFile()
        throws Exception
    {
      new File( LOCAL_REPO ).mkdir();
      new File( "target/build" ).mkdir();
      new File( "target/nut.xml" ).createNewFile();
      new File( "target/build/artifact.file" ).createNewFile();
      installer.execute(project,log);
      File installedArtifact = new File( LOCAL_REPO + "/local/group/artifact-0.0-SNAPSHOT.file" );
      assertTrue( installedArtifact.exists() );
      File installedArtifactNut = new File( LOCAL_REPO + "/local/group/artifact-0.0-SNAPSHOT.file.nut" );
      assertTrue( installedArtifactNut.exists() );
    }

    public void testInstallModules()
        throws Exception
    {
        Model model = new Model();
        model.addProperty( "basedir",    "target" );
        model.addProperty( "repository", LOCAL_REPO );
        model.addProperty( "build.directory",    "build" );
        model.addProperty( "project.groupId",    "local.group" );
        model.addProperty( "project.artifactId", "artifact" );
        model.addProperty( "project.version",    "0.0-SNAPSHOT" );
        model.addProperty( "project.packaging",  "modules" );
        NutProject prj = new NutProject(model);

      new File( LOCAL_REPO ).mkdir();
      new File( "target/build" ).mkdir();
      new File( "target/nut.xml" ).createNewFile();
      installer.execute(prj,log);
      File installedArtifact = new File( LOCAL_REPO + "/local/group/artifact-0.0-SNAPSHOT.modules" );
      assertFalse( installedArtifact.exists() );
      File installedArtifactNut = new File( LOCAL_REPO + "/local/group/artifact-0.0-SNAPSHOT.modules.nut" );
      assertTrue( installedArtifactNut.exists() );
    }

    public void testInstallIfArtifactFileIsNull()
        throws Exception
    {
      try
      {
        new File( LOCAL_REPO ).mkdir();
        new File( "target/build" ).mkdir();
        new File( "target/nut.xml" ).createNewFile();
        //new File( "target/build/artifact.file" ).createNewFile();
        //new File( "target/build/artifact.file" ).delete();
        //installer.execute(project,log);
        File installedArtifact = new File( LOCAL_REPO + "/local/group/artifact-0.0-SNAPSHOT.file" );
        assertTrue( installedArtifact.exists() );
      }
      catch(Exception e)
      {
        fail("ERROR");
      }
    }

}
