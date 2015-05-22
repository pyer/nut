package nut.project;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

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
//import ab.nut.artifact.ArtifactUtils;

import nut.logging.Log;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Goal;
import nut.model.Model;

import nut.project.InvalidDependencyVersionException;

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

    private Set artifacts;

    private Artifact artifact;

    private Map artifactMap;

    private Log log;
    
    // Building time
    private long time;
    // 
    boolean buildDone;
    boolean buildSuccess;

    // in case of failure
    private Exception cause;
    private String task;

    public NutProject( Model model )
    {
        this.model = model;
        this.time = 0;
        this.buildDone = false;
        this.buildSuccess = false;
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
/*
    public Exception getCause()
    {
        return cause;
    }

    public void setCause( Exception cause )
    {
        this.cause = cause;
    }

    public String getTask()
    {
        return task;
    }

    public void setCause( String task )
    {
        this.task = task;
    }
*/

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
        if ( other == this )
        {
            return true;
        }
        else if ( !( other instanceof NutProject ) )
        {
            return false;
        }
        else
        {
            NutProject otherProject = (NutProject) other;

            return getId().equals( otherProject.getId() );
        }
    }

}
