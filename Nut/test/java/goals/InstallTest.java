package nut.goals;

import nut.model.Layout;
import nut.model.Project;

import java.io.File;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class InstallTest
{
    private Project project;

    private void setupTest()
    {
        project = new Project();
        project.addProperty( "basedir", "target" );
        Layout layout = new Layout();
        layout.setTargetDirectory( "layout" );
        project.setLayout( layout );
        project.setGroupId( "local.group" );
        project.setArtifactId( "artifact" );
        project.setVersion( "0.0" );
        project.setPackaging( "file" );
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
        project.addProperty( "nut.home", "target/repo2" );
        new File( "target/repo2" ).mkdir();
        new File( "target/layout" ).mkdir();
        new File( "target/nut.xml" ).createNewFile();
        new File( "target/layout/artifact.file" ).createNewFile();
        new Install().execute(project);
        File installedArtifact = new File( "target/repo2/local/group/artifact-0.0-SNAPSHOT.file" );
        assertTrue( installedArtifact.exists() );
    }

    @Test
    public void testInstallModules()
        throws Exception
    {
        setupTest();
        project.addProperty( "nut.home", "target/repo3" );
        new File( "target/repo3" ).mkdir();
        new File( "target/layout" ).mkdir();
        new File( "target/nut.xml" ).createNewFile();
        project.setPackaging( "modules" );
        new Install().execute(project);
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
        project.addProperty( "nut.home", "target/repo4" );
        new File( "target/repo4" ).mkdir();
        new File( "target/layout" ).mkdir();
        new File( "target/nut.xml" ).createNewFile();
        project.setPackaging( "file" );
        new Install().execute(project);
        File installedArtifact = new File( "target/repo4/local/group/artifact-0.0-SNAPSHOT.file" );
        assertTrue( installedArtifact.exists() );
      }
      catch(Exception e)
      {
        fail("ERROR");
      }
    }

}
