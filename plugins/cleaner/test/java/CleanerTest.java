package nut.plugins;

import nut.logging.Log;

import nut.model.Model;
import nut.project.NutProject;

import java.io.File;
import java.io.Reader;
import java.util.Properties;

import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;

public class CleanerTest
{
    private final String LOCAL_TARGET = "target/local-target";
    private NutProject project;
    
    @BeforeTest
    public void setup()
    {
        Model model = new Model();
        model.addProperty( "basedir", "target" );
        model.addProperty( "build.directory", "local-target" );
        project = new NutProject(model);

    }

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
      File d = new File( LOCAL_TARGET );
      d.mkdirs();
      File f = new File( LOCAL_TARGET + "/dummy" );
      try
      {
        f.createNewFile();
        assertTrue( f.exists() );
        cleaner.execute(project, log);
      }
      catch( Exception e )
      {
        fail( e.getMessage() );
      }
      assertFalse( f.exists() );
      assertFalse( d.exists() );
    }
}

