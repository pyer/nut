package nut.model;

import nut.model.Build;
import nut.model.Dependency;

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

/**
 * 
 *         
 *         The <code>&lt;project&gt;</code> element is the root of
 * the descriptor.
 *         The following table lists all of the possible child
 * elements.
 *         
 *       
 * 
 * @version $Revision$ $Date$
 */
public class Model implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Declares to which version of project descriptor this POM conforms.
     */
    private String modelVersion;

    /**
     * A universally unique identifier for a project.
     * It is normal to
     * use a fully-qualified package name to
     * distinguish it from other
     * projects with a similar name (eg.
     * <code>org.apache.maven</code>).
     *           
     */
    private String groupId;

    /**
     * The identifier for this artifact that is unique within the
     * group given by the
     * group ID. An artifact is something that is
     * either produced or used by a project.
     * Examples of artifacts produced by Maven for a
     * project include: JARs, source and binary
     * distributions, and WARs.
     */
    private String artifactId;

    /**
     * The current version of the artifact produced by this project.
     */
    private String version;
    
    private String packaging = "modules";

    /**
     * The full name of the project.
     */
    private String name;

    /**
     * A detailed description of the project, used by Maven
     * whenever it needs to
     * describe the project, such as on the web site.
     * While this element can be specified as
     * CDATA to enable the use of HTML tags within the
     * description, it is discouraged to allow
     * plain text representation. If you need to modify
     * the index page of the generated web
     * site, you are able to specify your own instead
     * of adjusting this text.
     */
    private String description;

    /**
     * The location of the parent project, if one exists.
     * Values from the parent
     * project will be the default for this project if
     * they are left unspecified.
     */
    private String parent;

    /**
     * The URL to the project's homepage.
     */
    private String url;

    /**
     * Information required to build the project.
     */
    private Build build;

      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Get information required to build the project.
     * 
     * @return Build
     */
    public Build getBuild()
    {
        return this.build;
    }

    /**
     * Get 
     * 
     * A universally unique identifier for a project.
     * It is normal to
     * use a fully-qualified package name to
     * distinguish it from other
     * projects with a similar name (eg.
     * <code>org.apache.maven</code>).
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
     * Get the identifier for this artifact that is unique within
     * the group given by the
     * group ID. An artifact is something that is
     * either produced or used by a project.
     * Examples of artifacts produced by Maven for a
     * project include: JARs, source and binary
     * distributions, and WARs.
     * 
     * @return String
     */
    public String getArtifactId()
    {
        return this.artifactId;
    } //-- String getArtifactId() 

    /**
     * Get the current version of the artifact produced by this
     * project.
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
     * The type of artifact this project produces, for
     * example <code>jar</code>
     *   <code>war</code>
     *   <code>ear</code>
     *   <code>pom</code>.
     * Plugins can create their own packaging, and
     * therefore their own packaging types,
     * so this list does not contain all possible
     * types.
     * 
     *           
     * 
     * @return String
     */
    public String getPackaging()
    {
        return this.packaging;
    } //-- String getPackaging() 

    /**
     * Get the full name of the project.
     * 
     * @return String
     */
    public String getName()
    {
        return this.name;
    } //-- String getName() 

    /**
     * Get a detailed description of the project, used by Maven
     * whenever it needs to
     * describe the project, such as on the web site.
     * While this element can be specified as
     * CDATA to enable the use of HTML tags within the
     * description, it is discouraged to allow
     * plain text representation. If you need to modify
     * the index page of the generated web
     * site, you are able to specify your own instead
     * of adjusting this text.
     * 
     * @return String
     */
    public String getDescription()
    {
        return this.description;
    } //-- String getDescription() 

    /**
     * Get declares to which version of project descriptor this POM
     * conforms.
     * 
     * @return String
     */
    public String getModelVersion()
    {
        return this.modelVersion;
    } //-- String getModelVersion() 

    /**
     * Get the location of the parent project, if one exists.
     * Values from the parent
     * project will be the default for this project if
     * they are left unspecified.
     *
     * @return String
     */
    public String getParent()
    {
        return this.parent;
    } //-- String getParent() 

    /**
     * Get 
     * 
     * The URL to the project's homepage.
     * 
     *           
     * 
     * @return String
     */
    public String getUrl()
    {
        return this.url;
    } //-- String getUrl() 


    /**
     * Set information required to build the project.
     * 
     * @param build
     */
    public void setBuild( Build build )
    {
        this.build = build;
    }

    /**
     * Set 
     * 
     * A universally unique identifier for a project.
     * It is normal to
     * use a fully-qualified package name to
     * distinguish it from other
     * projects with a similar name (eg.
     * <code>org.apache.maven</code>).
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
     * Set the identifier for this artifact that is unique within
     * the group given by the
     * group ID. An artifact is something that is
     * either produced or used by a project.
     * Examples of artifacts produced by Maven for a
     * project include: JARs, source and binary
     * distributions, and WARs.
     * 
     * @param artifactId
     */

    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    } //-- void setArtifactId( String ) 

    /**
     * Set the current version of the artifact produced by this
     * project.
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
     * The type of artifact this project produces, for
     * example <code>jar</code>
     *   <code>war</code>
     *   <code>ear</code>
     *   <code>pom</code>.
     * Plugins can create their own packaging, and
     * therefore their own packaging types,
     * so this list does not contain all possible
     * types.
     * 
     *           
     * 
     * @param packaging
     */
    public void setPackaging( String packaging )
    {
        this.packaging = packaging;
    } //-- void setPackaging( String ) 

    /**
     * Set a detailed description of the project, used by Maven
     * whenever it needs to
     * describe the project, such as on the web site.
     * While this element can be specified as
     * CDATA to enable the use of HTML tags within the
     * description, it is discouraged to allow
     * plain text representation. If you need to modify
     * the index page of the generated web
     * site, you are able to specify your own instead
     * of adjusting this text.
     * 
     * @param description
     */
    public void setDescription( String description )
    {
        this.description = description;
    } //-- void setDescription( String ) 

    /**
     * Set the full name of the project.
     * 
     * @param name
     */
    public void setName( String name )
    {
        this.name = name;
    } //-- void setName( String ) 

    /**
     * Set declares to which version of project descriptor this POM
     * conforms.
     * 
     * @param modelVersion
     */
    public void setModelVersion( String modelVersion )
    {
        this.modelVersion = modelVersion;
    } //-- void setModelVersion( String ) 

    /**
     * Set the location of the parent project, if one exists.
     * Values from the parent
     * project will be the default for this project if
     * they are left unspecified.
     * 
     * @param parent
     */
    public void setParent( String parent )
    {
        this.parent = parent;
    } //-- void setParent( String ) 

    /**
     * Set 
     * 
     * The URL to the project's homepage.
     * 
     *           
     * 
     * @param url
     */
    public void setUrl( String url )
    {
        this.url = url;
    } //-- void setUrl( String ) 

    /**
     * @return the model id as <code>groupId:artifactId:version</code>
     */
    public String getId()
    {
        return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
    }
            
    /**
     * Field modules.
     */
    private List<String> modules;

    /**
     * Field dependencies.
     */
    private List<Dependency> dependencies;

    /**
     * Field properties.
     */
    private Properties properties;


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addDependency.
     * 
     * @param dependency
     */
    public void addDependency( Dependency dependency )
    {
        if ( !(dependency instanceof Dependency) )
        {
            throw new ClassCastException( "Model.addDependencies(dependency) parameter must be instanceof " + Dependency.class.getName() );
        }
        getDependencies().add( dependency );
    } //-- void addDependency( Dependency ) 

    /**
     * Method addModule.
     * 
     * @param string
     */
    public void addModule( String string )
    {
        if ( !(string instanceof String) )
        {
            throw new ClassCastException( "Model.addModules(string) parameter must be instanceof " + String.class.getName() );
        }
        getModules().add( string );
    } //-- void addModule( String ) 

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
     * Method getDependencies.
     * 
     * @return java.util.List
     */
    public List<Dependency> getDependencies()
    {
        if ( this.dependencies == null )
        {
            this.dependencies = new ArrayList<Dependency>();
        }
    
        return this.dependencies;
    } //-- java.util.List getDependencies() 

    /**
     * Method getModules.
     * 
     * @return java.util.List
     */
    public List<String> getModules()
    {
        if ( this.modules == null )
        {
            this.modules = new ArrayList<String>();
        }
    
        return this.modules;
    } //-- java.util.List getModules() 

    /**
     * Method getProperties.
     * 
     * @return java.util.Properties
     */
    public Properties getProperties()
    {
        if ( this.properties == null )
        {
            this.properties = new Properties();
        }
    
        return this.properties;
    } //-- java.util.Properties getProperties() 


    /**
     * Method removeDependency.
     * 
     * @param dependency
     */
    public void removeDependency( Dependency dependency )
    {
        if ( !(dependency instanceof Dependency) )
        {
            throw new ClassCastException( "Model.removeDependencies(dependency) parameter must be instanceof " + Dependency.class.getName() );
        }
        getDependencies().remove( dependency );
    } //-- void removeDependency( Dependency ) 

    /**
     * Method removeModule.
     * 
     * @param string
     */
    public void removeModule( String string )
    {
        if ( !(string instanceof String) )
        {
            throw new ClassCastException( "Model.removeModules(string) parameter must be instanceof " + String.class.getName() );
        }
        getModules().remove( string );
    } //-- void removeModule( String ) 

    /**
     * Set 
     * 
     * This element describes all of the dependencies
     * associated with a
     * project.
     * These dependencies are used to construct a
     * classpath for your
     * project during the build process. They are
     * automatically downloaded from the
     * repositories defined in this project.
     * 
     * @param dependencies
     */
    public void setDependencies( List<Dependency> dependencies )
    {
        this.dependencies = dependencies;
    } //-- void setDependencies( java.util.List ) 

    /**
     * Set the modules (sometimes called subprojects) to build as a
     * part of this
     * project. Each module listed is a relative path
     * to the directory containing the module.
     * 
     * @param modules
     */
    public void setModules( List<String> modules )
    {
        this.modules = modules;
    } //-- void setModules( java.util.List ) 

    /**
     * Set 
     * 
     * Properties that can be used throughout the POM
     * as a substitution, and
     * are used as filters in resources if enabled.
     * The format is
     * <code>&lt;name&gt;value&lt;/name&gt;</code>.
     * 
     *           
     * 
     * @param properties
     */
    public void setProperties( Properties properties )
    {
        this.properties = properties;
    } //-- void setProperties( java.util.Properties ) 

 
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
