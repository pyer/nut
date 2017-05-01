package nut.model;

import java.util.Properties;

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

    private String className;

    /**
     * The configuration as properties.
     */
    private Properties configuration;

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
     * Get the className of the goal.
     * @return String
     */
    public String getClassName()
    {
      if( this.className==null || this.className.isEmpty() ) {
        return this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
      }
      return this.className;
    }

    public String getClassName( String name )
    {
      if( name==null || name.isEmpty() )
        return "";
      if( this.className==null || this.className.isEmpty() ) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
      }
      return this.className;
    }

    /**
     * Set the className of the goal.
     * @param className
     */
    public void setClassName( String className )
    {
        this.className = className;
    }

    public boolean hasClassName()
    {
        return( (className!=null) && !className.isEmpty() );
    }

    /**
     * Get the configuration object
     * @return configuration
     */
    public Properties getConfiguration()
    {
        if ( this.configuration == null ) {
            this.configuration = new Properties();
        }
        return this.configuration;
    }

    public void setConfiguration( Properties config )
    {
        this.configuration = config;
    }

    public boolean hasConfiguration()
    {
        if ( this.configuration == null ) {
            this.configuration = new Properties();
        }
        return ! this.configuration.isEmpty();
    }

    /**
     * validate method
     */
    public void validate()
        throws ValidationException
    {
        if ( name == null )
            throw new ValidationException( "goal.name is null." );
        if ( name.length() < 2 )
            throw new ValidationException( "goal.name is empty." );
    }
}
