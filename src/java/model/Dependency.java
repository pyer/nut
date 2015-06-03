package nut.model;

import nut.model.ValidationException;

public class Dependency implements java.io.Serializable {

    //--------------------------/
    //- Class/Member Variables -/
    //--------------------------/

    /**
     * The project group that produced the dependency
     */
    private String groupId;

    /**
     * The unique id for an artifact produced by the project group
     */
    private String artifactId;

    /**
     * The version of the dependency
     */
    private String version = null;

    /**
     * The type of dependency. This defaults to <code>jar</code>.
     * While it usually represents the extension on the filename
     * of the dependency, that is not always the case.
     * A type can be mapped to a different extension.
     * The type often correspongs to the packaging
     * used, though this is also not always the case.
     * Some examples are <code>jar</code>,
     * <code>war</code>, <code>ejb-client</code>
     * and <code>test-jar</code>.
     * New types can be defined by plugins that set
     * <code>extensions</code> to <code>true</code>, so
     * this is not a complete list.
     */
    private String type = "jar";

    /**
     *
     * The scope of the dependency - compile, test, system
     * Used to calculate the various classpaths used for
     * compilation, testing, and so on.
     * It also assists in determining which artifacts
     * to include in a distribution of this project.
     */
    private String scope = "compile";

    /**
     * Field properties.
     */
    private java.util.Properties properties;

    //-----------/
    //- Methods -/
    //-----------/

    // -------------------------------------------------------------
    //-- String getGroupId() 
    public String getGroupId()
    {
        return this.groupId;
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    } //-- void setGroupId( String ) 

    //-- String getArtifactId() 
    public String getArtifactId()
    {
        return this.artifactId;
    } //-- String getArtifactId() 

    //-- void setArtifactId( String ) 
    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    } //-- void setArtifactId( String ) 

    public String getVersion()
    {
        return this.version;
    } //-- String getVersion() 

    public void setVersion( String version )
    {
        this.version = version;
    } //-- void setVersion( String ) 

    public String getType()
    {
        return this.type;
    } //-- String getType() 

    public void setType( String type )
    {
        this.type = type;
    } //-- void setType( String ) 

    public String getScope()
    {
        return this.scope;
    } //-- String getScope() 

    public void setScope( String scope )
    {
        this.scope = scope;
    } //-- void setScope( String ) 

    // -------------------------------------------------------------
    /**
     * Method getProperties.
     * 
     * @return java.util.Properties
     */
    public java.util.Properties getProperties()
    {
        if ( this.properties == null )
        {
            this.properties = new java.util.Properties();
        }
    
        return this.properties;
    } //-- java.util.Properties getProperties() 

    public void setProperties( java.util.Properties properties )
    {
        this.properties = properties;
    } //-- void setProperties( java.util.Properties ) 

    public void addProperty( String key, String value )
    {
        getProperties().put( key, value );
    } //-- void addProperty( String, String ) 

    public String getProperty( String property )
    {
        return getProperties().getProperty( property );
    }

    // -------------------------------------------------------------
    public String getId()
    {
        StringBuffer id = new StringBuffer();

        id.append( getGroupId() );
        id.append( ":" );
        id.append( getArtifactId() );
        id.append( ":" );
        id.append( getVersion() );

        return id.toString();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof Dependency ) )
        {
            return false;
        }

        Dependency d  = (Dependency) o;
        return getId().equals( d.getId() );
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getId().hashCode();
    }

    private String modelEncoding = "UTF-8";

    /**
     * Set an encoding used for reading/writing the model.
     *
     * @param modelEncoding the encoding used when reading/writing the model.
     */
    public void setModelEncoding( String modelEncoding )
    {
        this.modelEncoding = modelEncoding;
    }

    /**
     * @return the current encoding used when reading/writing this model.
     */
    public String getModelEncoding()
    {
        return modelEncoding;
    }

    /**
     * validate method
     * if a dependency version is undefined, it is the same as the project version
     */
    private static final String ID_REGEX = "[A-Za-z0-9_\\-.]+";

    public void validate( String projectVersion )
        throws ValidationException
    {
        if ( !groupId.matches( ID_REGEX ) )
            throw new ValidationException( "dependency.groupId '" + groupId + "' does not match a valid id pattern." );
        if ( !artifactId.matches( ID_REGEX ) )
            throw new ValidationException( "dependency.artifactId '" + artifactId + "' does not match a valid id pattern." );
        if ( version == null )
            this.version = projectVersion;
    }

}
