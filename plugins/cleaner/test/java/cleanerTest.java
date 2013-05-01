package nut.plugins;

import nut.logging.Log;

import nut.model.Model;
import nut.project.NutProject;

import junit.framework.TestCase;

import java.io.File;
import java.io.Reader;
import java.util.Properties;


public class cleanerTest
    extends TestCase
{
    /** Instance logger */
    private static Log log;

    private final String LOCAL_TARGET = "target/local-target";
    private NutProject project;
    
    public void setUp()
        throws Exception
    {
        super.setUp();
        Model model = new Model();
        model.addProperty( "basedir", "target" );
        model.addProperty( "build.directory", "local-target" );
        project = new NutProject(model);

        log = new Log();
    }

    public void testCreateTarget()
    {
      File d = new File( LOCAL_TARGET );
      d.mkdirs();
      assertTrue( d.exists() );
    }
    
    public void testExecute()
    {
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

