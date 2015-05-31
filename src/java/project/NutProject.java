package nut.project;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import nut.artifact.Artifact;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Goal;
import nut.model.Model;

import nut.project.BuildFailureException;
import nut.project.InvalidDependencyVersionException;

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
public class NutProject
{
    
    private Model model;

    private Artifact artifact;

    // Building time
    private long time;
    // 
    boolean buildDone;
    boolean buildSuccess;

    private Log log;

    // in case of failure
    private Exception cause;
    private String task;

    public NutProject( Model model )
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

    public void buildIsDone()
    {
        this.buildDone=true;
    }
    
    public boolean isSuccessful()
    {
        return buildSuccess;
    }

    public long getTime()
    {
        return time;
    }

    public void setStatus( long time, boolean success )
    {
        this.time = time;
        this.buildSuccess = success;
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

    public void setName( String name )
    {
        getModel().setName( name );
    }

    public String getName()
    {
        // TODO: this should not be allowed to be null.
        if ( getModel().getName() != null )
        {
            return getModel().getName();
        }
        else
        {
            return getId();
        }
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

        if ( build == null )
        {
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
        } else if ( !( other instanceof NutProject ) ) {
            return false;
        } else {
            NutProject otherProject = (NutProject) other;
            return getId().equals( otherProject.getId() );
        }
    }

    // ----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void executeGoal( String goalName, Properties config )
        throws BuildFailureException
    {
      try {
          log.debug("* execute goal: " + goalName);
          Class cls = Class.forName ("nut.goals." + goalName);
          Class[] cArg = new Class[2];
          cArg[0] = NutProject.class;
          cArg[1] = Properties.class;
          Method method = cls.getMethod("execute", cArg);
          method.invoke( cls, this, config );
      } catch (IllegalArgumentException e) {
          throw new BuildFailureException( e.getMessage() , e );
      } catch (IllegalAccessException e) {
          throw new BuildFailureException( e.getMessage() , e );
      } catch (InvocationTargetException e) {
          throw new BuildFailureException( e.getMessage() , e );
      } catch ( ClassNotFoundException e) {
          throw new BuildFailureException( "Goal " + goalName + " not found" , e );
      } catch (NoSuchMethodException e) {
          throw new BuildFailureException( "Method 'execute' not found" , e );
      } catch (SecurityException e) {
          throw new BuildFailureException( e.getMessage() , e );
      } catch (NullPointerException e) {
          throw new BuildFailureException( e.getMessage() , e );
      }
    }

}
