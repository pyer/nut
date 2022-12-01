package nut.goals;

import nut.build.Scanner;
import nut.goals.GoalException;
import nut.model.ParserException;
import nut.model.Project;
import nut.model.ValidationException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class InstallTest
{

    @Test
    public void testTargetExists()
    {
        String basedir = System.getProperty( "basedir", "." );
        File target = new File( basedir + "/target/test-target" );
        target.mkdir();
        assertTrue ( target.exists() );
    }

    @Test
    public void testRepositoryExists()
    {
        String basedir = System.getProperty( "basedir", "." );
        File repo = new File( basedir + "/target/test-repo" );
        repo.mkdir();
        assertTrue ( repo.exists() );
    }

    @Test
    public void testInstallJarRelease() throws IOException, ParserException, ValidationException, GoalException
    {
        String basedir = System.getProperty( "basedir", "." );
        Scanner scanner = new Scanner(basedir + "/test/resources/fullProject.yaml", false);
        List<Project> projects = scanner.getProjects();
        assertFalse( projects.isEmpty() );
        Project project = projects.get(0);
        project.setBaseDirectory(basedir);
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
        new Install().execute(project);
        assertTrue( installedFile.exists() );
    }

    @Test
    public void testInstallJarSnapshot() throws IOException, ParserException, ValidationException, GoalException
    {
        String basedir = System.getProperty( "basedir", "." );
        Scanner scanner = new Scanner(basedir + "/test/resources/fullProject.yaml", false);
        List<Project> projects = scanner.getProjects();
        assertFalse( projects.isEmpty() );
        Project project = projects.get(0);
        project.setVersionMode("-SNAPSHOT");
        project.setBaseDirectory(basedir);
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
        new Install().execute(project);
        assertTrue( installedFile.exists() );
    }

    @Test
    public void testInstallJarFileNoop() throws IOException, ParserException, ValidationException, GoalException
    {
        String basedir = System.getProperty( "basedir", "." );
        Scanner scanner = new Scanner(basedir + "/test/resources/fullProject.yaml", true);
        List<Project> projects = scanner.getProjects();
        assertFalse( projects.isEmpty() );
        Project project = projects.get(0);
        project.setBaseDirectory(basedir);
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
        new Install().execute(project);
        assertFalse( installedFile.exists() );
    }

    @Test
    public void testInstallModules() throws IOException, ParserException, ValidationException, GoalException
    {
        String basedir = System.getProperty( "basedir", "." );
        Scanner scanner = new Scanner(basedir + "/test/resources/modulesProject.yaml", false);
        List<Project> projects = scanner.getProjects();
        // As projects is empty, Install goal is not executed
        assertTrue( projects.isEmpty() );
    }

}
