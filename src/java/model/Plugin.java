package nut.model;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

//import java.util.Date;

/**
 * 
 *         
 * The <code>&lt;plugin&gt;</code> element contains
 * informations required for a plugin.
 *         
 *       
 * 
 * @version $Revision$ $Date$
 */
public class Plugin implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The group ID of the plugin in the repository.
     */
    private String groupId = "nut.plugins";

    /**
     * The artifact ID of the plugin in the repository.
     */
    private String artifactId;

    /**
     * The version (or valid range of versions) of the plugin to be
     * used.
     */
    private String version;

    private String goal;

    private Boolean skip = false;

    /**
     * The configuration as properties.
     */
    private java.util.Properties configuration;


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Get the group ID of the plugin in the repository.
     * 
     * @return String
     */
    public String getGroupId()
    {
        return this.groupId;
    } //-- String getGroupId() 

    /**
     * Get the artifact ID of the plugin in the repository.
     * 
     * @return String
     */
    public String getArtifactId()
    {
        return this.artifactId;
    } //-- String getArtifactId() 

    /**
     * Get the version (or valid range of versions) of the plugin
     * to be used.
     * 
     * @return String
     */
    public String getVersion()
    {
        if ( this.version == null )
        {
          this.version = System.getProperty( "nut.version", "1.0" );
        }
        return this.version;
    } //-- String getVersion() 

    /**
     * Get the goal of the plugin
     * 
     * @return String
     */
    public String getGoal()
    {
        return this.goal;
    } //-- String getGoal() 

    public Boolean getSkip()
    {
        return this.skip;
    }

    /**
     * Get the configuration object
     * 
     * @return configuration 
     */
    public java.util.Properties getConfiguration()
    {
        if ( this.configuration == null )
        {
            this.configuration = new java.util.Properties();
        }
        return this.configuration;
    }

    /**
     * 
     * @param  key
     * @return value
     */
    public String getConfigurationValue( String key )
    {
        return getConfiguration().getProperty( key );
    }
    /**
     * Set the group ID of the plugin in the repository.
     * 
     * @param groupId
     */
    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    /**
     * Set the artifact ID of the plugin in the repository.
     * 
     * @param artifactId
     */
    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    /**
     * Set the version (or valid range of versions) of the plugin
     * to be used.
     * 
     * @param version
     */
    public void setVersion( String version )
    {
        this.version = version;
    }

    /**
     * Set the goal of the plugin
     * 
     * @param goal
     */
    public void setGoal( String goal )
    {
        this.goal = goal;
    }

    public void setSkip( String skip )
    {
        this.skip = Boolean.valueOf(skip);
    }

    /**
     * Set the configuration object.
     * 
     * @param configuration
     */
    public void setConfiguration( java.util.Properties configuration )
    {
        this.configuration = configuration;
    }

    /**
     * 
     * @param key
     * @param value
     */
    public void setConfigurationValue( String key, String value )
    {
        getConfiguration().setProperty( key, value );
    }

    /**
     * @return the key of the plugin, ie <code>groupId:artifactId</code>
     */
    public String getKey()
    {
        return groupId + ":" + artifactId;
    }

    /**
     * @param groupId
     * @param artifactId
     * @return the key of the plugin, ie <code>groupId:artifactId</code>
     *
    public static String constructKey( String groupId, String artifactId )
    {
        return groupId + ":" + artifactId;
    }
*/
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object other )
    {
        if ( other instanceof Plugin )
        {
            Plugin otherPlugin = (Plugin) other;

            return getKey().equals( otherPlugin.getKey() );
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getKey().hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "Plugin [" + groupId + ":" + artifactId + ":" + version + "]";
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
}
