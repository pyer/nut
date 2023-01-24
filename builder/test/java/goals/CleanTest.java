package nut.goals;

import nut.annotations.Test;
import nut.goals.GoalException;
import nut.model.ParserException;
import nut.model.Project;
import nut.model.ValidationException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static nut.Assert.assertEquals;
import static nut.Assert.assertFalse;
import static nut.Assert.assertTrue;
import static nut.Assert.fail;

public class CleanTest
{

    @Test
    public void testCleanTarget() throws IOException, ParserException, ValidationException, GoalException
    {
        Project project = createProject("/test/resources/fullProject.yaml", false);
        String basedir = project.getBaseDirectory();
        // Create test target
        File d1 = new File( basedir + "/target/test-target" );
        d1.mkdirs();
        assertTrue( d1.exists() );
        // Create some files
        File d2 = new File( basedir + "/target/test-target/dummy" );
        d2.mkdirs();
        assertTrue( d2.exists() );
        File f1 = new File( basedir + "/target/test-target/empty.jar" );
        f1.createNewFile();
        assertTrue( f1.exists() );
        File f2 = new File( basedir + "/target/test-target/dummy/empty.txt" );
        f2.createNewFile();
        assertTrue( f2.exists() );
        // Real clean, files are deleted
        new Clean().execute(project);
        assertFalse( f1.exists() );
        assertFalse( f2.exists() );
        assertFalse( d1.exists() );
        assertFalse( d2.exists() );
    }

    @Test
    public void testCleanTargetNoop() throws IOException, ParserException, ValidationException, GoalException
    {
        Project project = createProject("/test/resources/fullProject.yaml", true);
        String basedir = project.getBaseDirectory();
        // Create test target
        File d1 = new File( basedir + "/target/test-target" );
        d1.mkdirs();
        assertTrue( d1.exists() );
        // Create some files
        File d2 = new File( basedir + "/target/test-target/dummy" );
        d2.mkdirs();
        assertTrue( d2.exists() );
        File f1 = new File( basedir + "/target/test-target/empty.jar" );
        f1.createNewFile();
        assertTrue( f1.exists() );
        File f2 = new File( basedir + "/target/test-target/dummy/empty.txt" );
        f2.createNewFile();
        assertTrue( f2.exists() );
        // Test noop, all files are present
        new Clean().execute(project);
        assertTrue( f1.exists() );
        assertTrue( f2.exists() );
        assertTrue( d1.exists() );
        assertTrue( d2.exists() );
    }

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
