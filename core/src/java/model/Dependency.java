package nut.model;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

//import java.util.Date;

/**
 * 
 *         
 * The <code>&lt;dependency&gt;</code> element contains
 * information about a dependency
 * of the project.
 *         
 *       
 * 
 * @version $Revision$ $Date$
 */
public class Dependency implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * 
     * 
     * The project group that produced the dependency,
     * e.g.
     * <code>org.apache.maven</code>.
     * 
     *           
     */
    private String groupId;

    /**
     * 
     * 
     * The unique id for an artifact produced by the
     * project group, e.g.
     * <code>maven-artifact</code>.
     * 
     *           
     */
    private String artifactId;

    /**
     * 
     * 
     * The version of the dependency, e.g.
     * <code>3.2.1</code>. In Maven 2, this can also be
     * specified as a range of versions.
     * 
     *           
     */
    private String version;

    /**
     * 
     * 
     * The type of dependency. This defaults to
     * <code>jar</code>. While it
     * usually represents the extension on the filename
     * of the dependency,
     * that is not always the case. A type can be
     * mapped to a different
     * extension and a classifier.
     * The type often correspongs to the packaging
     * used, though this is also
     * not always the case.
     * Some examples are <code>jar</code>,
     * <code>war</code>, <code>ejb-client</code>
     * and <code>test-jar</code>.
     * New types can be defined by plugins that set
     * <code>extensions</code> to <code>true</code>, so
     * this is not a complete list.
     * 
     *           
     */
    private String type = "jar";

    /**
     * This url will be provided to the user if the jar file cannot
     * be downloaded
     * from the central repository.
     */
    private String url;

    /**
     * 
     * 
     * The classifier of the dependency. This allows
     * distinguishing two artifacts
     * that belong to the same POM but were built
     * differently, and is appended to
     * the filename after the version. For example,
     * <code>jdk14</code> and <code>jdk15</code>.
     * 
     *           
     */
    private String classifier;

    /**
     * Field properties.
     */
    private java.util.Properties properties;

    /**
     *
     * The scope of the dependency -
     * <code>compile</code>, <code>runtime</code>,
     * <code>test</code>, <code>system</code>, and
     * <code>provided</code>. Used to
     * calculate the various classpaths used for
     * compilation, testing, and so on.
     * It also assists in determining which artifacts
     * to include in a distribution of
     * this project. For more information, see
     * <a
     * href="http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html">the
     * dependency mechanism</a>.
     * 
     */
    private String scope = "compile";

    /**
     * Field exclusions.
     */
    private java.util.List exclusions;

    /**
     * Indicates the dependency is optional for use of this
     * library. While the
     * version of the dependency will be taken into
     * account for dependency calculation if the
     * library is used elsewhere, it will not be passed
     * on transitively.
     */
    private boolean optional = false;


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addExclusion.
     * 
     * @param exclusion
     */
/*
    public void addExclusion( Exclusion exclusion )
    {
        if ( !(exclusion instanceof Exclusion) )
        {
            throw new ClassCastException( "Dependency.addExclusions(exclusion) parameter must be instanceof " + Exclusion.class.getName() );
        }
        getExclusions().add( exclusion );
    } //-- void addExclusion( Exclusion ) 
*/

    /**
     * Method addProperty.
     * 
     * @param key
     * @param value
     */
    public void addProperty( String key, String value )
    {
        getProperties().put( key, value );
    } //-- void addProperty( String, String ) 

    /**
     * Get 
     * 
     * The project group that produced the dependency,
     * e.g.
     * <code>org.apache.maven</code>.
     * 
     *           
     * 
     * @return String
     */
    public String getGroupId()
    {
        return this.groupId;
    } //-- String getGroupId() 

    /**
     * Get 
     * 
     * The unique id for an artifact produced by the
     * project group, e.g.
     * <code>maven-artifact</code>.
     * 
     *           
     * 
     * @return String
     */
    public String getArtifactId()
    {
        return this.artifactId;
    } //-- String getArtifactId() 

    /**
     * Get 
     * 
     * The classifier of the dependency. This allows
     * distinguishing two artifacts
     * that belong to the same POM but were built
     * differently, and is appended to
     * the filename after the version. For example,
     * <code>jdk14</code> and <code>jdk15</code>.
     * 
     *           
     * 
     * @return String
     */
    public String getClassifier()
    {
        return this.classifier;
    } //-- String getClassifier() 

    /**
     * Method getExclusions.
     * 
     * @return java.util.List
     */
/*
    public java.util.List getExclusions()
    {
        if ( this.exclusions == null )
        {
            this.exclusions = new java.util.ArrayList();
        }
    
        return this.exclusions;
    } //-- java.util.List getExclusions() 
*/
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


    /**
     * Get 
     * 
     * The scope of the dependency -
     * <code>compile</code>, <code>runtime</code>,
     * <code>test</code>, <code>system</code>, and
     * <code>provided</code>. Used to
     * calculate the various classpaths used for
     * compilation, testing, and so on.
     * It also assists in determining which artifacts
     * to include in a distribution of
     * this project. For more information, see
     * <a
     * href="http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html">the
     * dependency mechanism</a>.
     * 
     *           
     * 
     * @return String
     */
    public String getScope()
    {
        return this.scope;
    } //-- String getScope() 

    /**
     * Get this url will be provided to the user if the jar file
     * cannot be downloaded
     * from the central repository.
     * 
     * @return String
     */
    public String getUrl()
    {
        return this.url;
    } //-- String getUrl() 

    /**
     * Get 
     * 
     * The version of the dependency, e.g.
     * <code>3.2.1</code>. In Maven 2, this can also be
     * specified as a range of versions.
     * 
     *           
     * 
     * @return String
     */
    public String getVersion()
    {
        return this.version;
    } //-- String getVersion() 

    /**
     * Get 
     * 
     * The type of dependency. This defaults to
     * <code>jar</code>. While it
     * usually represents the extension on the filename
     * of the dependency,
     * that is not always the case. A type can be
     * mapped to a different
     * extension and a classifier.
     * The type often correspongs to the packaging
     * used, though this is also
     * not always the case.
     * Some examples are <code>jar</code>,
     * <code>war</code>, <code>ejb-client</code>
     * and <code>test-jar</code>.
     * New types can be defined by plugins that set
     * <code>extensions</code> to <code>true</code>, so
     * this is not a complete list.
     * 
     *           
     * 
     * @return String
     */
    public String getType()
    {
        return this.type;
    } //-- String getType() 

    /**
     * Get indicates the dependency is optional for use of this
     * library. While the
     * version of the dependency will be taken into
     * account for dependency calculation if the
     * library is used elsewhere, it will not be passed
     * on transitively.
     * 
     * @return boolean
     */
    public boolean isOptional()
    {
        return this.optional;
    } //-- boolean isOptional() 

    /**
     * Method removeExclusion.
     * 
     * @param exclusion
     */
/*
    public void removeExclusion( Exclusion exclusion )
    {
        if ( !(exclusion instanceof Exclusion) )
        {
            throw new ClassCastException( "Dependency.removeExclusions(exclusion) parameter must be instanceof " + Exclusion.class.getName() );
        }
        getExclusions().remove( exclusion );
    } //-- void removeExclusion( Exclusion ) 
*/

    /**
     * Set 
     * 
     * The project group that produced the dependency,
     * e.g.
     * <code>org.apache.maven</code>.
     * 
     *           
     * 
     * @param groupId
     */
    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    } //-- void setGroupId( String ) 

    /**
     * Set 
     * 
     * The unique id for an artifact produced by the
     * project group, e.g.
     * <code>maven-artifact</code>.
     * 
     *           
     * 
     * @param artifactId
     */
    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    } //-- void setArtifactId( String ) 

    /**
     * Set 
     * 
     * The classifier of the dependency. This allows
     * distinguishing two artifacts
     * that belong to the same POM but were built
     * differently, and is appended to
     * the filename after the version. For example,
     * <code>jdk14</code> and <code>jdk15</code>.
     * 
     *           
     * 
     * @param classifier
     */
    public void setClassifier( String classifier )
    {
        this.classifier = classifier;
    } //-- void setClassifier( String ) 

    /**
     * Set lists a set of artifacts that should be excluded from
     * this dependency's
     * artifact list when it comes to calculating
     * transitive dependencies.
     * 
     * @param exclusions
     */
/*
    public void setExclusions( java.util.List exclusions )
    {
        this.exclusions = exclusions;
    } //-- void setExclusions( java.util.List ) 
*/
    /**
     * Set indicates the dependency is optional for use of this
     * library. While the
     * version of the dependency will be taken into
     * account for dependency calculation if the
     * library is used elsewhere, it will not be passed
     * on transitively.
     * 
     * @param optional
     */
    public void setOptional( boolean optional )
    {
        this.optional = optional;
    } //-- void setOptional( boolean ) 

    /**
     * Set 
     * 
     * Properties about the dependency. Various plugins
     * allow you to
     * mark dependencies with properties. For example
     * the war plugin looks for a
     * <code>war.bundle</code> property, and if found
     * will include the
     * dependency in <code>WEB-INF/lib</code>.
     * 
     *           
     * 
     * @param properties
     */
    public void setProperties( java.util.Properties properties )
    {
        this.properties = properties;
    } //-- void setProperties( java.util.Properties ) 

    /**
     * Set 
     * 
     * The scope of the dependency -
     * <code>compile</code>, <code>runtime</code>,
     * <code>test</code>, <code>system</code>, and
     * <code>provided</code>. Used to
     * calculate the various classpaths used for
     * compilation, testing, and so on.
     * It also assists in determining which artifacts
     * to include in a distribution of
     * this project. For more information, see
     * <a
     * href="http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html">the
     * dependency mechanism</a>.
     * 
     *           
     * 
     * @param scope
     */
    public void setScope( String scope )
    {
        this.scope = scope;
    } //-- void setScope( String ) 

    /**
     * Set this url will be provided to the user if the jar file
     * cannot be downloaded
     * from the central repository.
     * 
     * @param url
     */
    public void setUrl( String url )
    {
        this.url = url;
    } //-- void setUrl( String ) 

    /**
     * Set 
     * 
     * The version of the dependency, e.g.
     * <code>3.2.1</code>. In Maven 2, this can also be
     * specified as a range of versions.
     * 
     *           
     * 
     * @param version
     */
    public void setVersion( String version )
    {
        this.version = version;
    } //-- void setVersion( String ) 

    /**
     * Set 
     * 
     * The type of dependency. This defaults to
     * <code>jar</code>. While it
     * usually represents the extension on the filename
     * of the dependency,
     * that is not always the case. A type can be
     * mapped to a different
     * extension and a classifier.
     * The type often correspongs to the packaging
     * used, though this is also
     * not always the case.
     * Some examples are <code>jar</code>,
     * <code>war</code>, <code>ejb-client</code>
     * and <code>test-jar</code>.
     * New types can be defined by plugins that set
     * <code>extensions</code> to <code>true</code>, so
     * this is not a complete list.
     * 
     *           
     * 
     * @param type
     */
    public void setType( String type )
    {
        this.type = type;
    } //-- void setType( String ) 


            
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return groupId + "/" + type + "s:" + artifactId + "-" + version;
    }
            
    /**
     * @return the management key as <code>groupId:artifactId:type</code>
     */
    public String getManagementKey()
    {
//  System.out.println( "ManagementKey: " + groupId + ":" + artifactId + ":" + type + ( classifier != null ? ":" + classifier : "")  );
      return groupId + ":" + artifactId + ":" + type + ( classifier != null ? ":" + classifier : "" );
    }
            
          
            
    /**
     * @return the key as <code>id:type</code>
     */
    public String getKey()
    {
        return getId() + ":" + getType();
    }

    /**
     * @return the groupId as artifact directory
     */
    public String getArtifactDirectory()
    {
        return getGroupId();
    }

    /**
     * @return the artifact name as <code>artifactId-version.extension</code> if &lt;jar/&gt; not set
     */
/*
    public String getArtifact()
    {
        // If the jar name has been explicty set then use that. This
        // is when the <jar/> element is explicity used in the POM.
        if ( getJar() != null)
        {
            return getJar();
        }

        String artifact;

        if ("ejb-client".equals(getType())) {
          artifact = getArtifactId() + "-" + getVersion() + "-client." + getExtension();
        } else {
          artifact = getArtifactId() + "-" + getVersion() + "." + getExtension();
        }

        return artifact;
    }

    public String getTypeDirectory()
    {
        String path;
        if (getType().equals("ejb-client")) {
            path = "ejbs";
        } else {
            path = getType() + "s";
        }

        return path;
    }
*/

/*
    public String getExtension()
    {
        if ("ejb".equals(getType()) || "ejb-client".equals(getType()) || "plugin".equals(getType()) || "aspect".equals(getType()) || "uberjar".equals(getType())) return "jar";
        return getType();
    }

    public boolean isAddedToClasspath()
    {
        return ("jar".equals(getType()) || "ejb".equals(getType()) || "ejb-client".equals(getType()) || "sar".equals(getType()));
    }
*/

    public boolean isPlugin()
    {
        return ("plugin".equals(getType()));
    }

    public String getProperty( String property )
    {
        return getProperties().getProperty( property );
    }

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
}
