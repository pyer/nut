package nut.goals;

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

