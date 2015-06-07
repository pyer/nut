package nut.model;

import nut.model.ValidationException;

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/

public class Goal implements java.io.Serializable
{

    //--------------------------/
    //- Class/Member Variables -/
    //--------------------------/

    private String name;

    private String type;

    /**
     * The configuration as properties.
     */
    private java.util.Properties configuration;

    /**
     * The current encoding used when reading/writing this model.
     */
    private String modelEncoding = "UTF-8";

    //-----------/
    //- Methods -/
    //-----------/

    /**
     * Get the name of the goal.
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Set the name of the goal.
     * @param name
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * Get the type of the goal.
     * @return String
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * Set the type of the goal.
     * @param type
     */
    public void setType( String type )
    {
        this.type = type;
    }

    private boolean hasType()
    {
        return ( (type!=null) && !type.isEmpty() );
    }

    /**
     * Get the Id, ie full class name, of the goal.
     * For example: compile java ==> CompileJava
     * @return String
     */
    public String getId()
    {
      String id = name.substring(0, 1).toUpperCase() + name.substring(1);
      if( hasType() ) {
        return id + type.substring(0, 1).toUpperCase() + type.substring(1);
      }
      return id;
    }

    public String getId( String name )
    {
      if( name==null || name.isEmpty() )
        return "";
      return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * Get the configuration object
     * @return configuration 
     */
    public java.util.Properties getConfiguration()
    {
        if ( this.configuration == null ) {
            this.configuration = new java.util.Properties();
        }
        return this.configuration;
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
     * @param  key
     * @return value
     */
    public String getConfigurationValue( String key )
    {
        return getConfiguration().getProperty( key );
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
     * The current encoding used when reading/writing this model.
     */
    public String getModelEncoding()
    {
        return modelEncoding;
    }

    public void setModelEncoding( String modelEncoding )
    {
        this.modelEncoding = modelEncoding;
    }

    /**
     * validate method
     */
    public void validate()
        throws ValidationException
    {
        if ( name == null )
            throw new ValidationException( "goal.name is null." );
        if ( name.length() < 1 )
            throw new ValidationException( "goal.name is empty." );
    }
}
