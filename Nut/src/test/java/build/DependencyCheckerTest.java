package nut.build;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class DependencyCheckerTest
{

    @Test
    public void testHashCodeNullSafe()
    {
        new DependencyChecker().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertNotNull( new DependencyChecker() );
    }

    @Test
    public void testEqualsIdentity()
    {
        DependencyChecker thing = new DependencyChecker();
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new DependencyChecker().toString() );
    }

}
/*


public class DependencyChecker
{
    private Log log;

    // ----------------------------------------------------------------------
    public DependencyChecker()
    {
      log = new Log();
    }

    // ----------------------------------------------------------------------
    public void checkProject(Project project) throws DependencyNotFoundException
    {
      for ( Iterator it = project.getDependencies().iterator(); it.hasNext(); ) {
          Dependency dep = (Dependency) it.next();
//          Artifact artifact = new Artifact( dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getType() );

          if( artifact.isPresent() ) {
            log.debug( "  OK");
            return;
          }
          try {

*/

