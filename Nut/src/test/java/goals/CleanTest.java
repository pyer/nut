package nut.goals;

import nut.build.Scanner;
import nut.model.Project;

import java.io.File;
import java.util.List;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class CleanTest
{
    @Test
    public void testExecute()
    {
      try {
        String TARGET = "target/tget";
        Scanner scanner = new Scanner("src/test/resources/fullProject.yml");
        List<Project> projects = scanner.getProjects();
        assertFalse( projects.isEmpty() );
        Project project = projects.get(0);
        project.setBaseDirectory(".");
        // Create test target
        File d1 = new File( "target/tget" );
        d1.mkdirs();
        assertTrue( d1.exists() );
        // Create some files
        File d2 = new File( "target/tget/dummy" );
        d2.mkdirs();
        assertTrue( d2.exists() );
        File f1 = new File( "target/tget/empty.jar" );
        f1.createNewFile();
        assertTrue( f1.exists() );
        File f2 = new File( "target/tget/dummy/empty.txt" );
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
        //assertFalse( f1.exists() );
        assertFalse( f2.exists() );
        assertFalse( d1.exists() );
        assertFalse( d2.exists() );
      } catch( Exception e ) {
        fail( e.getMessage() );
      }
    }
}

/*

targetDirectory:       target/tget


      Project project = new Project();
      Layout layout = new Layout();
      layout.setTargetDirectory( "local-target" );
      project.addProperty( "basedir", "target" );
      project.setLayout( layout );

      File d = new File( LOCAL_TARGET );
      d.mkdirs();
      File f = new File( LOCAL_TARGET + "/dummy" );
      try
      {
        f.createNewFile();
        assertTrue( f.exists() );
        new Clean().execute(project);
      }
      catch( Exception e )
      {
        fail( e.getMessage() );
      }
      assertFalse( f.exists() );
      assertFalse( d.exists() );


public void execute(Project project, boolean noop) throws GoalException
*/
