package nut.goals;

import nut.model.Build;
import nut.model.Model;
import nut.project.Project;

import java.io.File;
import java.util.Properties;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class InstallTest
{
    private Model model = new Model();
    
    private void setupTest()
    {
        model.addProperty( "basedir", "target" );
        Build build = new Build();
        build.setTargetDirectory( "build" );
        model.setBuild( build );
        model.setGroupId( "local.group" );
        model.setArtifactId( "artifact" );
        model.setVersion( "0.0" );
        model.setPackaging( "file" );
    }

    @Test
    public void testRepository()
    {
        File repo = new File( "target/repo1" );
        repo.mkdir();
        //System.out.println( repo.getAbsolutePath() );
        assertTrue ( repo.exists() );
    }

    @Test
    public void testBasicInstallFile()
        throws Exception
    {
        setupTest();
        model.addProperty( "nut.home", "target/repo2" );
        new File( "target/repo2" ).mkdir();
        new File( "target/build" ).mkdir();
        new File( "target/nut.xml" ).createNewFile();
        new File( "target/build/artifact.file" ).createNewFile();
        Project project = new Project(model);
        Properties config = new Properties();
        Install.execute(project,config);
        File installedArtifact = new File( "target/repo2/local/group/artifact-0.0-SNAPSHOT.file" );
        assertTrue( installedArtifact.exists() );
    }

    @Test
    public void testInstallModules()
        throws Exception
    {
        setupTest();
        model.addProperty( "nut.home", "target/repo3" );
        new File( "target/repo3" ).mkdir();
        new File( "target/build" ).mkdir();
        new File( "target/nut.xml" ).createNewFile();
        model.setPackaging( "modules" );

        Project project = new Project(model);
        Properties config = new Properties();
        Install.execute(project,config);
        File installedArtifact = new File( "target/repo3/local/group/artifact-0.0-SNAPSHOT.modules" );
        assertFalse( installedArtifact.exists() );
    }

    @Test
    public void testInstallIfArtifactFileIsNull()
        throws Exception
    {
      try
      {
        setupTest();
        model.addProperty( "nut.home", "target/repo4" );
        new File( "target/repo4" ).mkdir();
        new File( "target/build" ).mkdir();
        new File( "target/nut.xml" ).createNewFile();
        model.setPackaging( "file" );
        Project project = new Project(model);
        Properties config = new Properties();
        Install.execute(project,config);
        File installedArtifact = new File( "target/repo4/local/group/artifact-0.0-SNAPSHOT.file" );
        assertTrue( installedArtifact.exists() );
      }
      catch(Exception e)
      {
        fail("ERROR");
      }
    }

}
