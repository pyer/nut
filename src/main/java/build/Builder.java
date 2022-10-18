package nut.build;

import nut.logging.Log;
import nut.model.Project;

import nut.goals.GoalException;

import nut.goals.Clean;
import nut.goals.Compile;
import nut.goals.Install;
import nut.goals.Pack;
import nut.goals.Run;
import nut.goals.Test;

import nut.interpolation.Interpolator;

import java.io.File;
import java.io.IOException;

import java.util.List;


public class Builder
{
    private Log log;
    private String goal;
    private List<Project> projects;

    // --------------------------------------------------------------------------------
    public Builder(String goal)
    {
      log = new Log();
      this.goal = goal;
    }

    // --------------------------------------------------------------------------------
    /*
     * returns 0 if success
     * returns 9 if not
     */
    public int build( Project project ) {
      boolean fail = false;
      log.debug("Build project " + project.getId() + " goal='" + goal +"'");
      if ( "model".equals(goal) ) {
          log.info( "Effective model of " + project.getId() + "\n" + project.effectiveNut());
      } else {
        if ( "modules".equals(project.getPattern()) ) {
          return 0;
        }
        try {
          project.start();
          // Interpolate
          Interpolator interpolator = new Interpolator();
          project = interpolator.interpolatedProject( project );
          // Check dependencies
          DependencyChecker depChecker = new DependencyChecker();
          depChecker.checkProject( project );
          // Achieve goal
          if( "build".equals(goal) ) {
            new Clean().execute(project);
            new Compile().execute(project);
            new Compile("test").execute(project);
            new Test().execute(project);
            new Pack().execute(project);
            new Install().execute(project);
          } else if( "clean".equals(goal) ) {
            new Clean().execute(project);
          } else if( "compile".equals(goal) ) {
            new Compile().execute(project);
          } else if( "test".equals(goal) ) {
            new Compile("test").execute(project);
            new Test().execute(project);
          } else if( "pack".equals(goal) ) {
            new Pack().execute(project);
          } else if( "install".equals(goal) ) {
            new Install().execute(project);
          } else if( "run".equals(goal) ) {
            new Run().execute(project);
          } else {
            log.error("Unknown goal '" + goal + "'.");
            fail = true;
          }
        } catch (DependencyNotFoundException e) {
          log.error(e.getMessage());
          fail = true;
        } catch ( GoalException e ) {
          log.error(e.getMessage());
          fail = true;
        } catch ( Exception e ) {
          log.error(e.getMessage());
          fail = true;
        }

        if( fail ) {
          project.failure();
          log.failure( project.getId() );
          return 9;
        }
        project.success();
      }
      return 0;
    }
}

