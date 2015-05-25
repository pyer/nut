package nut.model;

import nut.model.Build;
import nut.model.Dependency;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

/**
 * 
 * project is the root of the model descriptor.
 * The following table lists all of the possible child elements.
 *   - groupID
 *   - artifactID
 *   - version
 *   - packaging
 *   - description
 *   - build
 *   - dependencies
 *   - properties
 *
 */
public class Model implements java.io.Serializable
{
    /**
     * the encoding used when reading/writing the model.
     */
    private String modelEncoding = "UTF-8";

    public String getModelEncoding()
    {
        return modelEncoding;
    }          

    public void setModelEncoding( String modelEncoding )
    {
        this.modelEncoding = modelEncoding;
    }

    /**
     * Declares to which version of project descriptor this POM conforms.
     */
    private String modelVersion;

    public String getModelVersion()
    {
        return this.modelVersion;
    }
 
    public void setModelVersion( String modelVersion )
    {
        this.modelVersion = modelVersion;
    }

    /**
     * A universally unique identifier for a project.
     * It is normal to use a fully-qualified package name to
     * distinguish it from other projects with a similar name.
     */
    private String groupId;

    public String getGroupId()
    {
        return this.groupId;
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    /**
     * The identifier for this artifact that is unique within the
     * group given by the group ID.
     * An artifact is something that is either produced or used by a project.
     */
    private String artifactId;

    public String getArtifactId()
    {
        return this.artifactId;
    }

    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    /**
     * The current version of the artifact produced by this project.
     */
    private String version;
    
    public String getVersion()
    {
        return this.version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    /**
     * The packaging of artifact this project produces, for example: jar, zip
     * modules is a special packaging type.
     */
    private String packaging = "modules";

    public String getPackaging()
    {
        return this.packaging;
    }

    public void setPackaging( String packaging )
    {
        this.packaging = packaging;
    }

    /**
     * The full name of the project.
     */
    private String name;
    
    public String getName()
    {
        return this.name;
    }

    public void setName( String name )
    {
        this.name = name;
    }
    
    /**
     * A detailed description of the project,
     * whenever it needs to describe the project, such as on the web site.
     */

    private String description;

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    /**
     * Informations required to build the project.
     */
    private Build build;

    public Build getBuild()
    {
        return this.build;
    }

    public void setBuild( Build build )
    {
        this.build = build;
    }


    //-----------------/
    //- Other members -/
    //-----------------/

    private List<Dependency> dependencies;
    private List<String> modules;
    private Properties properties;

    //-----------/
    //- Methods -/
    //-----------/

    public String getId()
    {
        return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
    }

    /**
     * add methods
     */
    public void addDependency( Dependency dependency )
    {
        if ( !(dependency instanceof Dependency) )
        {
            throw new ClassCastException( "Model.addDependencies(dependency) parameter must be instanceof " + Dependency.class.getName() );
        }
        getDependencies().add( dependency );
    }

    public void addModule( String string )
    {
        if ( !(string instanceof String) )
        {
            throw new ClassCastException( "Model.addModules(string) parameter must be instanceof " + String.class.getName() );
        }
        getModules().add( string );
    }

    public void addProperty( String key, String value )
    {
        getProperties().put( key, value );
    }

    /**
     * get methods
     */
    public List<Dependency> getDependencies()
    {
        if ( this.dependencies == null )
        {
            this.dependencies = new ArrayList<Dependency>();
        }
    
        return this.dependencies;
    }

    public List<String> getModules()
    {
        if ( this.modules == null )
        {
            this.modules = new ArrayList<String>();
        }
    
        return this.modules;
    }

    public Properties getProperties()
    {
        if ( this.properties == null )
        {
            this.properties = new Properties();
        }
    
        return this.properties;
    }

    /**
     * remove methods
     */
    public void removeDependency( Dependency dependency )
    {
        if ( !(dependency instanceof Dependency) )
        {
            throw new ClassCastException( "Model.removeDependencies(dependency) parameter must be instanceof " + Dependency.class.getName() );
        }
        getDependencies().remove( dependency );
    }

    public void removeModule( String string )
    {
        if ( !(string instanceof String) )
        {
            throw new ClassCastException( "Model.removeModules(string) parameter must be instanceof " + String.class.getName() );
        }
        getModules().remove( string );
    }

    /**
     * set  methods
     */
    public void setDependencies( List<Dependency> dependencies )
    {
        this.dependencies = dependencies;
    }
    
    public void setModules( List<String> modules )
    {
        this.modules = modules;
    }

    public void setProperties( Properties properties )
    {
        this.properties = properties;
    }
 
}
