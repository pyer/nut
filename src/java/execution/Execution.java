package nut.execution;

import nut.logging.Log;

import nut.artifact.Artifact;
import nut.artifact.InvalidArtifactRTException;

import nut.model.Goal;

import nut.project.NutProject;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Execution
{
    private Map buildFailuresByProject = new HashMap();
    private Map pluginContextsByProjectAndPluginKey = new HashMap();
    private Map buildSuccessesByProject = new HashMap();
    private NutProject project;
    private Log log;

    public Execution( NutProject project, Log log )
    {
        this.project = project;
        this.log = log;
    }
    
    public void executeGoals( List goals )
        throws BuildFailureException
    {
        log.info( "Building " + project.getName() );
        log.debug( "[" + project.getId() + "]");
        for ( Iterator it = goals.iterator(); it.hasNext(); )
        {
            String goal = (String)it.next();
            //log.debug( "* execute goal " + goal );
            if ( goal.equals( "build" ) ) {
                 List buildGoals = project.getBuild().getGoals();
                 executeGoals( buildGoals );
            } else {
                executeGoal( goal, project );
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void executeGoal( String goal, NutProject project )
        throws BuildFailureException
    {
      try {
          log.debug(goal);
          Class cls = Class.forName ("nut.goals.goal");
          Method method = cls.getMethod(goal);
      } catch ( ClassNotFoundException e) {
          throw new BuildFailureException( "goal is unknown" , e );
      } catch (NoSuchMethodException e) {
          throw new BuildFailureException( goal + " is unknown" , e );
      } catch (SecurityException e) {
          throw new BuildFailureException( e.getMessage() , e );
      } catch (NullPointerException e) {
          throw new BuildFailureException( e.getMessage() , e );
      }
    }
}
