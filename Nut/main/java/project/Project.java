package nut.project;

import java.io.IOException;
import java.io.StringWriter;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import nut.artifact.Artifact;

import nut.interpolation.Interpolator;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Goal;
import nut.model.Model;
import nut.model.XmlWriter;
import nut.model.JsonWriter;

import nut.project.BuildException;
import nut.project.DependencyChecker;
import nut.project.DependencyNotFoundException;

import nut.logging.Log;

/**
 * The concern of the project is provide runtime values based on the model. <p/>
 * The values in the model remain untouched but during the process of building a
 * project notions like inheritance and interpolation can be added. This allows
 * to have an entity which is useful in a runtime while preserving the model so
 * that it can be marshalled and unmarshalled without being tainted by runtime
 * requirements. <p/>We need to leave the model intact because we don't want
 * the following:
 * <ol>
 * <li>We don't want interpolated values being written back into the model.
 * <li>We don't want inherited values being written back into the model.
 * </ol>
 */
public class Project
{

    private Model model;

    private Artifact artifact;

    // Building time
    private long time;
    //
    private boolean buildDone;
    private boolean buildSuccess;

    private Log log;

    public Project( Model model )
    {
        this.model = model;
        this.time = 0;
        this.buildDone = false;
        this.buildSuccess = false;
        this.log = new Log();
    }

    // ----------------------------------------------------------------------
    public boolean isBuilt()
    {
        return this.buildDone;
    }

    public boolean isSuccessful()
    {
        return buildSuccess;
    }

    public long getTime()
    {
        return time;
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public Artifact getArtifact()
    {
        return artifact;
    }

    public void setArtifact( Artifact artifact )
    {
        this.artifact = artifact;
    }

    //@todo I would like to get rid of this. jvz.
    public Model getModel()
    {
        return model;
    }

    public void setDependencies( List<Dependency> dependencies )
    {
        getModel().setDependencies( dependencies );
    }

    public List<Dependency> getDependencies()
    {
        return getModel().getDependencies();
    }

    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Delegate to the model
    // ----------------------------------------------------------------------
    public String getId()
    {
        return getModel().getId();
    }

    public void setGroupId( String groupId )
    {
        getModel().setGroupId( groupId );
    }

    public String getGroupId()
    {
        return getModel().getGroupId();
    }

    public void setArtifactId( String artifactId )
    {
        getModel().setArtifactId( artifactId );
    }

    public String getArtifactId()
    {
        return getModel().getArtifactId();
    }

    public void setVersion( String version )
    {
        getModel().setVersion( version );
    }

    public String getVersion()
    {
        return getModel().getVersion();
    }

    public void setPackaging( String packaging )
    {
        getModel().setPackaging( packaging );
    }

    public String getPackaging()
    {
        return getModel().getPackaging();
    }

    public void setDescription( String description )
    {
        getModel().setDescription( description );
    }

    public String getDescription()
    {
        return getModel().getDescription();
    }

    public Build getBuild()
    {
        Build build = getModel().getBuild();
        if ( build == null ) {
            build = new Build();
            getModel().setBuild( build );
        }
        return build;
    }

    // ----------------------------------------------------------------------

    public List<String> getModules()
    {
        return getModel().getModules();
    }

    public boolean equals( Object other )
    {
        if ( other == this ) {
            return true;
        } else if ( !( other instanceof Project ) ) {
            return false;
        } else {
            Project otherProject = (Project) other;
            return getId().equals( otherProject.getId() );
        }
    }

    // ----------------------------------------------------------------------
    public void effectiveXmlModel()
    {
      try {
          XmlWriter modelWriter = new XmlWriter();
          StringWriter sWriter  = new StringWriter();
          modelWriter.writeModel( sWriter, model );
          log.info( "Effective XML model of " + model.getId() );
          log.info( "nut.xml\n" + sWriter.toString() );
      } catch (IOException e) {
          log.error("IOException");
      }
    }

    public void effectiveJsonModel()
    {
      try {
          JsonWriter modelWriter = new JsonWriter();
          StringWriter sWriter  = new StringWriter();
          modelWriter.writeModel( sWriter, model );
          log.info( "Effective JSON model of " + model.getId() );
          log.info( "nut.json\n" + sWriter.toString() );
      } catch (IOException e) {
          log.error("IOException");
      }
    }

    // ----------------------------------------------------------------------
    // TODO: replace strings as ${xx} by their values
    public void interpolateModel()
    {
      Interpolator tor = new Interpolator();
      this.model = tor.interpolatedModel( this.model );
    }

    // ----------------------------------------------------------------------
    // check if every dependency is in the local repository
    // if not try to download it from repositories defined in nut.xml
    public void checkDependencies()
    {
      for ( Iterator it = getDependencies().iterator(); it.hasNext(); ) {
          Dependency dep = (Dependency) it.next();
          Artifact artifactDep = new Artifact( dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getType() );
          try {
            log.debug( "* check " + artifactDep.toString() );
            new DependencyChecker( artifactDep, getModel().getRepositories() );
          } catch (DependencyNotFoundException e) {
              log.failure( "Missing dependency " + dep.getId() + "(" + e.getMessage() + ")" );
          } catch (SecurityException e) {
            log.error("Unreadable dependency " + dep.getId() );
          }
      }
    }

    // ----------------------------------------------------------------------
    // returns classpath for TestNG child process
    public String getDependenciesClassPath()
    {
      String classpath = "";
      for ( Iterator it = getDependencies().iterator(); it.hasNext(); ) {
          Dependency dep = (Dependency) it.next();
          if( "test".equals(dep.getScope()) || "compile".equals(dep.getScope()) ) {
              Artifact artifactDep = new Artifact( dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getType() );
              classpath = classpath + ":" + artifactDep.getPath();
          }
      }
      return classpath;
    }

    // ----------------------------------------------------------------------
    public void build( String targetGoal, boolean noopMode )
    {
      try {
        time = System.currentTimeMillis();

        List<Goal> goals = getBuild().getGoals();
        for ( Iterator g = goals.iterator(); g.hasNext(); ) {
            Goal goal = (Goal)g.next();
            if( "build".equals(targetGoal) || goal.getName().equals(targetGoal) ) {
              // build is done if at least one goal is executed
              buildDone=true;
              if( noopMode ) {
                log.info( "NOOP: " + goal.getClassName() + " " + getId() );
              } else {
                executeGoal( goal.getClassName(), goal.configuration() );
              }
            }
        }
        time = System.currentTimeMillis() - time;
        if( buildDone ) {
          buildSuccess = true;
        } else {
          log.warning( "No goal '" + targetGoal + "' in the packaging '" + getPackaging() + "' for " + getId() );
        }
      }
      catch ( BuildException e ) {
        time = System.currentTimeMillis() - time;
        log.failure( getId() );
      }
    }

    // ----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private void executeGoal( String goalName, Properties config )
        throws BuildException
    {
      try {
          log.debug("* execute goal: " + goalName);
          Class cls = Class.forName ("nut.goals." + goalName);
          Class[] cArg = new Class[2];
          cArg[0] = Project.class;
          cArg[1] = Properties.class;
          Method method = cls.getMethod("execute", cArg);
          method.invoke( cls, this, config );
      } catch (IllegalArgumentException e) {
          throw new BuildException( e.getMessage() , e );
      } catch (IllegalAccessException e) {
          throw new BuildException( e.getMessage() , e );
      } catch (InvocationTargetException e) {
          throw new BuildException( e.getMessage() , e );
      } catch ( ClassNotFoundException e) {
          throw new BuildException( "Goal " + goalName + " not found" , e );
      } catch (NoSuchMethodException e) {
          throw new BuildException( "Method 'execute' not found in " + goalName, e );
      } catch (SecurityException e) {
          throw new BuildException( e.getMessage() , e );
      } catch (NullPointerException e) {
          throw new BuildException( e.getMessage() , e );
      }
    }
}
