package nut.model;

/*
Repository defines way to seek missing artifacts, ie dependencies
For example, get testng jar file from Maven central repository is done by:
wget http://search.maven.org/remotecontent?filepath=org/testng/testng/6.8.7/testng-6.8.7.jar -O testng-6.8.7.jar

  <repository>
    <name>Maven Repository Switchboard</name>
    <layout>maven</layout>
    <url>http://search.maven.org/remotecontent</url>
  </repository>

TO DO:

<repositories>
  <repository>
  ...
  </repository>
</repositories>
*/

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/
import nut.model.ValidationException;

public class Repository implements java.io.Serializable
{
    //--------------------------/
    //- Class/Member Variables -/
    //--------------------------/

    private String name;
    private String layout = "nut";  // default layout
    private String url;

    //-----------/
    //- Methods -/
    //-----------/

    /**
     * Get the name.
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Set the name.
     * @param name
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * Get the layout.
     * @return String
     */
    public String getLayout()
    {
        return this.layout;
    }

    /**
     * Set the layout.
     * @param layout
     */
    public void setLayout( String layout )
    {
        this.layout = layout;
    }

    /**
     * Get the url.
     * @return String
     */
    public String getURL()
    {
        return this.url;
    }

    /**
     * Set the url.
     * @param url
     */
    public void setURL( String url )
    {
        this.url = url;
    }

    public void validate()
        throws ValidationException
    {
        if ( name == null || name.isEmpty() )
            throw new ValidationException( "Undefined name for repository " + url );
        if ( url == null || url.isEmpty() )
            throw new ValidationException( "Undefined url for repository " + name );
        if ( layout == null || layout.isEmpty() )
            throw new ValidationException( "Undefined layout for repository " + name );
    }

}
