package nut.model;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Repository;
import nut.model.ValidationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
 *   - repositories
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
     * Declares a parent xml file which contains common values, for example version or groupId.
     */
    private String parent;

    public String getParent()
    {
        return this.parent;
    }

    public void setParent( String parent )
    {
        this.parent = parent;
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

    private List<String> modules;
    private List<Dependency> dependencies;
    private List<Repository> repositories;
    private Properties properties;

    //-----------/
    //- Methods -/
    //-----------/

    public String getId()
    {
        return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
    }

    /**
     * get methods
     */
    public List<String> getModules()
    {
        if ( this.modules == null ) {
            this.modules = new ArrayList<String>();
        }
        return this.modules;
    }

    public List<Dependency> getDependencies()
    {
        if ( this.dependencies == null ) {
            this.dependencies = new ArrayList<Dependency>();
        }
        return this.dependencies;
    }

    public List<Repository> getRepositories()
    {
        if ( this.repositories == null ) {
            this.repositories = new ArrayList<Repository>();
        }
        return this.repositories;
    }

    public Properties getProperties()
    {
        if ( this.properties == null ) {
            this.properties = new Properties();
        }
        return this.properties;
    }

    /**
     * set  methods
     */
    public void setModules( List<String> modules )
    {
        this.modules = modules;
    }

    public void setDependencies( List<Dependency> dependencies )
    {
        this.dependencies = dependencies;
    }

    public void setRepositories( List<Repository> repositories )
    {
        this.repositories = repositories;
    }

    public void setProperties( Properties properties )
    {
        this.properties = properties;
    }

    public void addProperty( String key, String value )
    {
        getProperties().put( key, value );
    }
    /**
     * validate method
     */
    private static final String ID_REGEX = "[A-Za-z0-9_\\-.]+";

    public void validate()
        throws ValidationException
    {
        validateStringNotEmpty( "groupId", groupId );
        if ( !groupId.matches( ID_REGEX ) )
            throw new ValidationException( "groupId '" + groupId + "' does not match a valid id pattern." );
        validateStringNotEmpty( "artifactId", artifactId );
        if ( !artifactId.matches( ID_REGEX ) )
            throw new ValidationException( "artifactId '" + artifactId + "' does not match a valid id pattern." );

        validateStringNotEmpty( "version", version );
        validateStringNotEmpty( "packaging", packaging );

        if ( !getModules().isEmpty() && !"modules".equals( packaging ) ) {
            throw new ValidationException( "Packaging '" + packaging +
                                                "' is invalid. Aggregator projects require 'modules' as packaging." );
        }

        for ( Iterator it = getDependencies().iterator(); it.hasNext(); )
        {
            Dependency dep = (Dependency) it.next();
            dep.validate(version);
        }

        for ( Iterator it = getRepositories().iterator(); it.hasNext(); )
        {
            Repository repo = (Repository) it.next();
            repo.validate();
        }

        if ( build != null ) {
            build.validate();
        }
    }

    // ----------------------------------------------------------------------
    // Field validation
    // ----------------------------------------------------------------------
    private void validateStringNotEmpty( String fieldName, String string )
        throws ValidationException
    {
        if ( string == null )
            throw new ValidationException( "'" + fieldName + "' is null." );
        if ( string.length() <1 )
            throw new ValidationException( "'" + fieldName + "' is empty." );
    }

}