package nut.goals;

import nut.annotations.Test;
import nut.goals.GoalException;
import nut.model.ParserException;
import nut.model.Project;
import nut.model.ValidationException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static nut.Assert.assertFalse;
import static nut.Assert.assertTrue;
import static nut.Assert.fail;

public class InstallTest
{

    @Test
    public void testTargetExists()
    {
        String basedir = System.getProperty("nut.basedir", ".");
        File target = new File( basedir + "/target/test-target" );
        target.mkdir();
        assertTrue ( target.exists() );
    }

    @Test
    public void testRepositoryExists()
    {
        String basedir = System.getProperty("nut.basedir", ".");
        File repo = new File( basedir + "/target/test-repo" );
        repo.mkdir();
        assertTrue ( repo.exists() );
    }

    @Test
    public void testInstallJarRelease() throws IOException, ParserException, ValidationException, GoalException
    {
        Project project = createProject("/test/resources/fullProject.yaml", false);
        String basedir = project.getBaseDirectory();
        project.setRepository(basedir + "/target/test-repo");
        // Create test repository
        File repo = new File(basedir + "/target/test-repo");
        repo.mkdir();
        // Create test target
        File target = new File(basedir + "/target/test-target");
        target.mkdir();
        File targetFile = new File(basedir + "/target/test-target/full.jar");
        targetFile.createNewFile();
        assertTrue( targetFile.exists() );
        File installedFile = new File(basedir + "/target/test-repo/nut/test/full-3.0.jar");
        installedFile.delete();
        // Real install
        assertFalse( installedFile.exists() );
        // Execute goal
        new Install().execute(project);
        assertTrue( installedFile.exists() );
    }

    @Test
    public void testInstallJarSnapshot() throws IOException, ParserException, ValidationException, GoalException
    {
        Project project = createProject("/test/resources/fullProject.yaml", false);
        String basedir = project.getBaseDirectory();
        project.setVersionMode("-SNAPSHOT");
        project.setRepository(basedir + "/target/test-repo");
        // Create test repository
        File repo = new File(basedir + "/target/test-repo");
        repo.mkdir();
        // Create test target
        File target = new File(basedir + "/target/test-target");
        target.mkdir();
        File targetFile = new File(basedir + "/target/test-target/full.jar");
        targetFile.createNewFile();
        assertTrue( targetFile.exists() );
        File installedFile = new File(basedir + "/target/test-repo/nut/test/full-3.0-SNAPSHOT.jar");
        installedFile.delete();
        // Real install
        assertFalse( installedFile.exists() );
        // Execute goal
        new Install().execute(project);
        assertTrue( installedFile.exists() );
    }

    @Test
    public void testInstallJarFileNoop() throws IOException, ParserException, ValidationException, GoalException
    {
        Project project = createProject("/test/resources/fullProject.yaml", true);
        String basedir = project.getBaseDirectory();
        project.setRepository(basedir + "/target/test-repo");
        // Create test repository
        File repo = new File(basedir + "/target/test-repo");
        repo.mkdir();
        // Create test target
        File target = new File(basedir + "/target/test-target");
        target.mkdir();
        File targetFile = new File(basedir + "/target/test-target/full.jar");
        targetFile.createNewFile();
        assertTrue( targetFile.exists() );
        File installedFile = new File(basedir + "/target/test-repo/nut/test/full-3.0-SNAPSHOT.jar");
        installedFile.delete();
        // Test noop
        assertFalse( installedFile.exists() );
        // Execute goal
        new Install().execute(project);
        assertFalse( installedFile.exists() );
    }

/*
    @Test
    public void testInstallModules() throws IOException, ParserException, ValidationException, GoalException
    {
        Project project = createProject("/test/resources/modulesProject.yaml", false);
        String basedir = project.getBaseDirectory();
        // Execute goal
        new Install().execute(project);
        assert what ?
    }
*/

    private Project createProject( String nutFile, boolean noop) {
        String basedir = System.getProperty("nut.basedir", ".");
        try {
          Project project = new Project(noop);
          project.setBaseDirectory(basedir);
          File file = new File(basedir + nutFile);
          project.parseFile( file );
          project.validate();
          return project;
        } catch( Exception e) {
          // IOException, ParserException, ValidationException
          fail(e.getMessage());
        }
        return null;
    }

}
