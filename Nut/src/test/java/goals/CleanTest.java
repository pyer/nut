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

public class CleanTest
{
    @Test
    public void testExecute() throws IOException, ParserException, ValidationException, GoalException
    {
        Scanner scanner = new Scanner("src/test/resources/fullProject.yml");
        List<Project> projects = scanner.getProjects();
        assertFalse( projects.isEmpty() );
        Project project = projects.get(0);
        project.setBaseDirectory(".");
        // Create test target
        File d1 = new File( "target/test-target" );
        d1.mkdirs();
        assertTrue( d1.exists() );
        // Create some files
        File d2 = new File( "target/test-target/dummy" );
        d2.mkdirs();
        assertTrue( d2.exists() );
        File f1 = new File( "target/test-target/empty.jar" );
        f1.createNewFile();
        assertTrue( f1.exists() );
        File f2 = new File( "target/test-target/dummy/empty.txt" );
        f2.createNewFile();
        assertTrue( f2.exists() );
        // Test noop
        new Clean().execute(project, true);
        assertTrue( f1.exists() );
        assertTrue( f2.exists() );
        assertTrue( d1.exists() );
        assertTrue( d2.exists() );
        // Real clean
        new Clean().execute(project, false);
        assertFalse( f1.exists() );
        assertFalse( f2.exists() );
        assertFalse( d1.exists() );
        assertFalse( d2.exists() );
    }
}
