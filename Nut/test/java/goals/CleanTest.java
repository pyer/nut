package nut.goals;

import nut.model.Layout;
import nut.model.Project;

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
    }
}
