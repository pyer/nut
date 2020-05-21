package nut.goals;

import nut.model.Layout;
import nut.model.Model;
import nut.project.Project;

import java.io.File;

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
      Layout layout = new Layout();
      layout.setTargetDirectory( "local-target" );
      Model model = new Model();
      model.addProperty( "basedir", "target" );
      model.setLayout( layout );
      Project project = new Project();
      project.setModel(model);

      File d = new File( LOCAL_TARGET );
      d.mkdirs();
      File f = new File( LOCAL_TARGET + "/dummy" );
      try
      {
        f.createNewFile();
        assertTrue( f.exists() );
        Clean.execute(project);
      }
      catch( Exception e )
      {
        fail( e.getMessage() );
      }
      assertFalse( f.exists() );
      assertFalse( d.exists() );
    }
}
