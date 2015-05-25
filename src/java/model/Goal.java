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
     * Get the type of the goal.
     * @return String
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * Get the configuration object
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
     * Set the name of the goal.
     * @param name
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * Set the type of the goal.
     * @param type
     */
    public void setType( String type )
    {
        this.type = type;
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
