package nut.goals;

import nut.logging.Log;

import nut.model.Build;
import nut.model.Model;
import nut.project.Project;

import java.io.File;
import java.io.Reader;
import java.util.Properties;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class CleanTest
{
    private final String LOCAL_TARGET = "target/local-target";
    
    @Test
    public void testCreateTarget()
    {
      File d = new File( LOCAL_TARGET );
      d.mkdirs();
      assertTrue( d.exists() );
    }
    
    @Test
    public void testExecute()
    {
      Log log = new Log();
      Build build = new Build();
      build.setTargetDirectory( "local-target" );
      Model model = new Model();
      model.addProperty( "basedir", "target" );
      model.setBuild( build );
      Project project = new Project(model);

      File d = new File( LOCAL_TARGET );
      d.mkdirs();
      File f = new File( LOCAL_TARGET + "/dummy" );
      try
      {
/*
File directory = new File (".");
log.info("Current directory's canonical path: " + directory.getCanonicalPath());
log.info("Current directory's absolute  path: " + directory.getAbsolutePath());
*/
        f.createNewFile();
        assertTrue( f.exists() );
        Clean.execute(project, null);
      }
      catch( Exception e )
      {
        fail( e.getMessage() );
      }
      assertFalse( f.exists() );
      assertFalse( d.exists() );
    }
}

