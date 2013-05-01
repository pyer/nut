package nut.model;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.util.Locale;

import nut.model.Build;
import nut.model.Dependency;
//import ab.nut.model.DependencyManagement;
//import ab.nut.model.Exclusion;
//import ab.nut.model.Extension;
//import ab.nut.model.FileSet;
import nut.model.Model;
import nut.model.Plugin;
import nut.model.PluginContainer;
//import ab.nut.model.Resource;
//import ab.nut.model.Scm;
//import ab.nut.model.UnitTest;

import nut.xml.pull.XmlPullParser;
import nut.xml.pull.XmlPullParserException;

/**
 * Class modelXpp3Reader.
 * 
 * @version $Revision$ $Date$
 */
public class xmlReader {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * If set the parser will be loaded with all single characters
     * from the XHTML specification.
     * The entities used:
     * <ul>
     * <li>http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent</li>
     * <li>http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent</li>
     * <li>http://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent</li>
     * </ul>
     */
    private boolean addDefaultEntities = true;


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Sets the state of the "add default entities" flag.
     * 
     * @param addDefaultEntities
     */
    public void setAddDefaultEntities( boolean addDefaultEntities )
    {
        this.addDefaultEntities = addDefaultEntities;
    } //-- void setAddDefaultEntities( boolean ) 

    /**
     * Returns the state of the "add default entities" flag.
     * 
     * @return boolean
     */
    public boolean getAddDefaultEntities()
    {
        return addDefaultEntities;
    } //-- boolean getAddDefaultEntities() 

    /**
     * Method getBooleanValue.
     * 
     * @param s
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return boolean
     */
    public boolean getBooleanValue( String s, String attribute, XmlPullParser parser )
        throws XmlPullParserException
    {
        return getBooleanValue( s, attribute, parser, null );
    } //-- boolean getBooleanValue( String, String, XmlPullParser ) 

    /**
     * Method getBooleanValue.
     * 
     * @param s
     * @param defaultValue
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return boolean
     */
    public boolean getBooleanValue( String s, String attribute, XmlPullParser parser, String defaultValue )
        throws XmlPullParserException
    {
        if ( s != null && s.length() != 0 )
        {
            return Boolean.valueOf( s ).booleanValue();
        }
        if ( defaultValue != null )
        {
            return Boolean.valueOf( defaultValue ).booleanValue();
        }
        return false;
    } //-- boolean getBooleanValue( String, String, XmlPullParser, String ) 

    /**
     * Method getByteValue.
     * 
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return byte
     */
    public byte getByteValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            try
            {
                return Byte.valueOf( s ).byteValue();
            }
            catch ( NumberFormatException e )
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unable to parse element '" + attribute + "', must be a byte", parser, null );
                }
            }
        }
        return 0;
    } //-- byte getByteValue( String, String, XmlPullParser, boolean ) 

    /**
     * Method getCharacterValue.
     * 
     * @param s
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return char
     */
    public char getCharacterValue( String s, String attribute, XmlPullParser parser )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            return s.charAt( 0 );
        }
        return 0;
    } //-- char getCharacterValue( String, String, XmlPullParser ) 

    /**
     * Method getDateValue.
     * 
     * @param s
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return java.util.Date
     */
    public java.util.Date getDateValue( String s, String attribute, XmlPullParser parser )
        throws XmlPullParserException
    {
        return getDateValue( s, attribute, null, parser );
    } //-- java.util.Date getDateValue( String, String, XmlPullParser ) 

    /**
     * Method getDateValue.
     * 
     * @param s
     * @param parser
     * @param dateFormat
     * @param attribute
     * @throws XmlPullParserException
     * @return java.util.Date
     */
    public java.util.Date getDateValue( String s, String attribute, String dateFormat, XmlPullParser parser )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            if ( dateFormat == null )
            {
                return new java.util.Date( Long.valueOf( s ).longValue() );
            }
            else
            {
                DateFormat dateParser = new java.text.SimpleDateFormat( dateFormat, Locale.US );
                try
                {
                    return dateParser.parse( s );
                }
                catch ( java.text.ParseException e )
                {
                    throw new XmlPullParserException( e.getMessage() );
                }
            }
        }
        return null;
    } //-- java.util.Date getDateValue( String, String, String, XmlPullParser ) 

    /**
     * Method getDoubleValue.
     * 
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return double
     */
    public double getDoubleValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            try
            {
                return Double.valueOf( s ).doubleValue();
            }
            catch ( NumberFormatException e )
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unable to parse element '" + attribute + "', must be a floating point number", parser, null );
                }
            }
        }
        return 0;
    } //-- double getDoubleValue( String, String, XmlPullParser, boolean ) 

    /**
     * Method getFloatValue.
     * 
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return float
     */
    public float getFloatValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            try
            {
                return Float.valueOf( s ).floatValue();
            }
            catch ( NumberFormatException e )
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unable to parse element '" + attribute + "', must be a floating point number", parser, null );
                }
            }
        }
        return 0;
    } //-- float getFloatValue( String, String, XmlPullParser, boolean ) 

    /**
     * Method getIntegerValue.
     * 
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return int
     */
    public int getIntegerValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            try
            {
                return Integer.valueOf( s ).intValue();
            }
            catch ( NumberFormatException e )
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unable to parse element '" + attribute + "', must be an integer", parser, null );
                }
            }
        }
        return 0;
    } //-- int getIntegerValue( String, String, XmlPullParser, boolean ) 

    /**
     * Method getLongValue.
     * 
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return long
     */
    public long getLongValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            try
            {
                return Long.valueOf( s ).longValue();
            }
            catch ( NumberFormatException e )
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unable to parse element '" + attribute + "', must be a long integer", parser, null );
                }
            }
        }
        return 0;
    } //-- long getLongValue( String, String, XmlPullParser, boolean ) 

    /**
     * Method getRequiredAttributeValue.
     * 
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return String
     */
    public String getRequiredAttributeValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s == null )
        {
            if ( strict )
            {
                throw new XmlPullParserException( "Missing required value for attribute '" + attribute + "'", parser, null );
            }
        }
        return s;
    } //-- String getRequiredAttributeValue( String, String, XmlPullParser, boolean ) 

    /**
     * Method getShortValue.
     * 
     * @param s
     * @param strict
     * @param parser
     * @param attribute
     * @throws XmlPullParserException
     * @return short
     */
    public short getShortValue( String s, String attribute, XmlPullParser parser, boolean strict )
        throws XmlPullParserException
    {
        if ( s != null )
        {
            try
            {
                return Short.valueOf( s ).shortValue();
            }
            catch ( NumberFormatException e )
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unable to parse element '" + attribute + "', must be a short integer", parser, null );
                }
            }
        }
        return 0;
    } //-- short getShortValue( String, String, XmlPullParser, boolean ) 

    /**
     * Method getTrimmedValue.
     * 
     * @param s
     * @return String
     */
    public String getTrimmedValue( String s )
    {
        if ( s != null )
        {
            s = s.trim();
        }
        return s;
    } //-- String getTrimmedValue( String ) 

    /**
     * Method parseBuild.
     * 
     * @param tagName
     * @param strict
     * @param parser
     * @throws IOException
     * @throws XmlPullParserException
     * @return Build
     */
    private Build parseBuild( String tagName, XmlPullParser parser, boolean strict )
        throws IOException, XmlPullParserException
    {
        Build build = new Build();
        java.util.Set<String> parsed = new java.util.HashSet<String>();
        while ( parser.nextTag() == XmlPullParser.START_TAG )
        {
            if ( parser.getName().equals( "sourceDirectory" )  )
            {
                if ( parsed.contains( "sourceDirectory" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "sourceDirectory" );
                build.setSourceDirectory( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "testSourceDirectory" )  )
            {
                if ( parsed.contains( "testSourceDirectory" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "testSourceDirectory" );
                build.setTestSourceDirectory( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "outputDirectory" )  )
            {
                if ( parsed.contains( "outputDirectory" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "outputDirectory" );
                build.setOutputDirectory( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "testOutputDirectory" )  )
            {
                if ( parsed.contains( "testOutputDirectory" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "testOutputDirectory" );
                build.setTestOutputDirectory( getTrimmedValue( parser.nextText()) );
            }


            else if ( parser.getName().equals( "directory" )  )
            {
                if ( parsed.contains( "directory" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "directory" );
                build.setDirectory( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "plugins" )  )
            {
                if ( parsed.contains( "plugins" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "plugins" );
                java.util.List<Plugin> pluginContainer = new java.util.ArrayList<Plugin>();
                build.setPlugins( pluginContainer );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( parser.getName().equals( "plugin" ) )
                    {
                        pluginContainer.add( parsePlugin( "plugin", parser, strict ) );
                    }
                    else if ( strict )
                    {
                        throw new XmlPullParserException( "Unrecognized association: '" + parser.getName() + "'", parser, null );
                    }
                    else
                    {
                        // swallow up to end tag since this is not valid
                        while ( parser.next() != XmlPullParser.END_TAG ) {}
                    }
                }
            }
            else
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unrecognized tag: '" + parser.getName() + "'", parser, null );
                }
                else
                {
                    // swallow up to end tag since this is not valid
                    while ( parser.next() != XmlPullParser.END_TAG ) {}
                }
            }
        }
        return build;
    } //-- Build parseBuild( String, XmlPullParser, boolean ) 

    /**
     * Method parseDependency.
     * 
     * @param tagName
     * @param strict
     * @param parser
     * @throws IOException
     * @throws XmlPullParserException
     * @return Dependency
     */
    private Dependency parseDependency( String tagName, XmlPullParser parser, boolean strict )
        throws IOException, XmlPullParserException
    {
        Dependency dependency = new Dependency();
        java.util.Set<String> parsed = new java.util.HashSet<String>();
        while ( parser.nextTag() == XmlPullParser.START_TAG )
        {
            if ( parser.getName().equals( "groupId" )  )
            {
                if ( parsed.contains( "groupId" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "groupId" );
                dependency.setGroupId( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "artifactId" )  )
            {
                if ( parsed.contains( "artifactId" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "artifactId" );
                dependency.setArtifactId( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "version" )  )
            {
                if ( parsed.contains( "version" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "version" );
                dependency.setVersion( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "type" )  )
            {
                if ( parsed.contains( "type" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "type" );
                dependency.setType( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "classifier" )  )
            {
                if ( parsed.contains( "classifier" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "classifier" );
                dependency.setClassifier( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "scope" )  )
            {
                if ( parsed.contains( "scope" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "scope" );
                dependency.setScope( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "url" )  )
            {
                if ( parsed.contains( "url" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "url" );
                dependency.setUrl( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "properties" )  )
            {
                if ( parsed.contains( "properties" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "properties" );
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    String key = parser.getName();
                    String value = parser.nextText().trim();
                    dependency.addProperty( key, value );
                }
            }
            else
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unrecognized tag: '" + parser.getName() + "'", parser, null );
                }
                else
                {
                    // swallow up to end tag since this is not valid
                    while ( parser.next() != XmlPullParser.END_TAG ) {}
                }
            }
        }
        return dependency;
    } //-- Dependency parseDependency( String, XmlPullParser, boolean ) 


    /**
     * Method parseModel.
     * 
     * @param tagName
     * @param strict
     * @param parser
     * @throws IOException
     * @throws XmlPullParserException
     * @return Model
     */
    private Model parseModel( String tagName, XmlPullParser parser, boolean strict )
        throws IOException, XmlPullParserException
    {
        Model model = new Model();
        java.util.Set<String> parsed = new java.util.HashSet<String>();
        int eventType = parser.getEventType();
        boolean foundRoot = false;
        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
//System.out.println( "modelXpp3Reader: <" + parser.getName() +">" );
                if ( parser.getName().equals( tagName ) )
                {
                    foundRoot = true;
                }
                else if ( parser.getName().equals( "modelVersion" )  )
                {
                    if ( parsed.contains( "modelVersion" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "modelVersion" );
                    model.setModelVersion( getTrimmedValue( parser.nextText()) );
                }
                else if ( parser.getName().equals( "groupId" )  )
                {
                    if ( parsed.contains( "groupId" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "groupId" );
                    model.setGroupId( getTrimmedValue( parser.nextText()) );
                }
                else if ( parser.getName().equals( "artifactId" )  )
                {
                    if ( parsed.contains( "artifactId" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "artifactId" );
                    model.setArtifactId( getTrimmedValue( parser.nextText()) );
                }
                else if ( parser.getName().equals( "version" )  )
                {
                    if ( parsed.contains( "version" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "version" );
                    model.setVersion( getTrimmedValue( parser.nextText()) );
                }
                else if ( parser.getName().equals( "packaging" )  )
                {
                    if ( parsed.contains( "packaging" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "packaging" );
                    model.setPackaging( getTrimmedValue( parser.nextText()) );
                }
                else if ( parser.getName().equals( "name" )  )
                {
                    if ( parsed.contains( "name" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "name" );
                    model.setName( getTrimmedValue( parser.nextText()) );
                }
                else if ( parser.getName().equals( "description" )  )
                {
                    if ( parsed.contains( "description" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "description" );
                    model.setDescription( getTrimmedValue( parser.nextText()) );
                }
                else if ( parser.getName().equals( "parent" )  )
                {
                    if ( parsed.contains( "parent" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "parent" );
                    model.setParent( getTrimmedValue( parser.nextText()) );
                }
                else if ( parser.getName().equals( "url" )  )
                {
                    if ( parsed.contains( "url" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "url" );
                    model.setUrl( getTrimmedValue( parser.nextText()) );
                }
                else if ( parser.getName().equals( "build" )  )
                {
                    if ( parsed.contains( "build" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "build" );
                    model.setBuild( parseBuild( "build", parser, strict ) );
                }
                else if ( parser.getName().equals( "modules" )  )
                {
                    if ( parsed.contains( "modules" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "modules" );
                    java.util.List<String> modules = new java.util.ArrayList<String>();
                    model.setModules( modules );
                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        if ( parser.getName().equals( "module" ) )
                        {
                            modules.add( getTrimmedValue( parser.nextText()) );
                        }
                        else if ( strict )
                        {
                            throw new XmlPullParserException( "Unrecognised association: '" + parser.getName() + "'", parser, null );
                        }
                        else
                        {
                            // swallow up to end tag since this is not valid
                            while ( parser.next() != XmlPullParser.END_TAG ) {}
                        }
                    }
                }
                else if ( parser.getName().equals( "dependencies" )  )
                {
                    if ( parsed.contains( "dependencies" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "dependencies" );
                    java.util.List<Dependency> dependencies = new java.util.ArrayList<Dependency>();
                    model.setDependencies( dependencies );
                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        if ( parser.getName().equals( "dependency" ) )
                        {
                            dependencies.add( parseDependency( "dependency", parser, strict ) );
                        }
                        else if ( strict )
                        {
                            throw new XmlPullParserException( "Unrecognised association: '" + parser.getName() + "'", parser, null );
                        }
                        else
                        {
                            // swallow up to end tag since this is not valid
                            while ( parser.next() != XmlPullParser.END_TAG ) {}
                        }
                    }
                }
                else if ( parser.getName().equals( "properties" )  )
                {
                    if ( parsed.contains( "properties" ) )
                    {
                        throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                    }
                    parsed.add( "properties" );
                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        String key = parser.getName();
                        String value = parser.nextText().trim();
                        model.addProperty( key, value );
                    }
                }
                else if ( strict )
                {
                    throw new XmlPullParserException( "Unrecognised tag: '" + parser.getName() + "'", parser, null );
                }
            }
            eventType = parser.next();
        }
        return model;
    } //-- Model parseModel( String, XmlPullParser, boolean ) 


    /**
     * Method parsePlugin.
     * 
     * @param tagName
     * @param strict
     * @param parser
     * @throws IOException
     * @throws XmlPullParserException
     * @return Plugin
     */
    private Plugin parsePlugin( String tagName, XmlPullParser parser, boolean strict )
        throws IOException, XmlPullParserException
    {
        Plugin plugin = new Plugin();
        java.util.Set<String> parsed = new java.util.HashSet<String>();
        while ( parser.nextTag() == XmlPullParser.START_TAG )
        {
            if ( parser.getName().equals( "groupId" )  )
            {
                if ( parsed.contains( "groupId" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "groupId" );
                plugin.setGroupId( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "artifactId" )  )
            {
                if ( parsed.contains( "artifactId" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "artifactId" );
                plugin.setArtifactId( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "version" )  )
            {
                if ( parsed.contains( "version" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "version" );
                plugin.setVersion( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "goal" )  )
            {
                if ( parsed.contains( "goal" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "goal" );
                plugin.setGoal( getTrimmedValue( parser.nextText()) );
            }
            else if ( parser.getName().equals( "skip" )  )
            {
                if ( parsed.contains( "skip" ) )
                {
                    throw new XmlPullParserException( "Duplicated tag: '" + parser.getName() + "'", parser, null );
                }
                parsed.add( "skip" );
                plugin.setSkip( getTrimmedValue( parser.nextText()) );
            }
            else
            {
                if ( strict )
                {
                    throw new XmlPullParserException( "Unrecognised tag: '" + parser.getName() + "'", parser, null );
                }
                else
                {
                    // swallow up to end tag since this is not valid
                    while ( parser.next() != XmlPullParser.END_TAG ) {}
                }
            }
        }
        return plugin;
    } //-- Plugin parsePlugin( String, XmlPullParser, boolean ) 


    /**
     * @see ReaderFactory#newXmlReader
     * 
     * @param reader
     * @param strict
     * @throws IOException
     * @throws XmlPullParserException
     * @return Model
     */
    public Model read( Reader reader, boolean strict )
        throws IOException, XmlPullParserException
    {
        XmlPullParser parser = new XmlPullParser();

//System.out.println( "modelXpp3Reader:" );
    
        parser.setInput( reader );
    
        if ( addDefaultEntities )
        {
            // ----------------------------------------------------------------------
            // Latin 1 entities
            // ----------------------------------------------------------------------
    
            parser.defineEntityReplacementText( "nbsp", "\u00a0" );
            parser.defineEntityReplacementText( "iexcl", "\u00a1" );
            parser.defineEntityReplacementText( "cent", "\u00a2" );
            parser.defineEntityReplacementText( "pound", "\u00a3" );
            parser.defineEntityReplacementText( "curren", "\u00a4" );
            parser.defineEntityReplacementText( "yen", "\u00a5" );
            parser.defineEntityReplacementText( "brvbar", "\u00a6" );
            parser.defineEntityReplacementText( "sect", "\u00a7" );
            parser.defineEntityReplacementText( "uml", "\u00a8" );
            parser.defineEntityReplacementText( "copy", "\u00a9" );
            parser.defineEntityReplacementText( "ordf", "\u00aa" );
            parser.defineEntityReplacementText( "laquo", "\u00ab" );
            parser.defineEntityReplacementText( "not", "\u00ac" );
            parser.defineEntityReplacementText( "shy", "\u00ad" );
            parser.defineEntityReplacementText( "reg", "\u00ae" );
            parser.defineEntityReplacementText( "macr", "\u00af" );
            parser.defineEntityReplacementText( "deg", "\u00b0" );
            parser.defineEntityReplacementText( "plusmn", "\u00b1" );
            parser.defineEntityReplacementText( "sup2", "\u00b2" );
            parser.defineEntityReplacementText( "sup3", "\u00b3" );
            parser.defineEntityReplacementText( "acute", "\u00b4" );
            parser.defineEntityReplacementText( "micro", "\u00b5" );
            parser.defineEntityReplacementText( "para", "\u00b6" );
            parser.defineEntityReplacementText( "middot", "\u00b7" );
            parser.defineEntityReplacementText( "cedil", "\u00b8" );
            parser.defineEntityReplacementText( "sup1", "\u00b9" );
            parser.defineEntityReplacementText( "ordm", "\u00ba" );
            parser.defineEntityReplacementText( "raquo", "\u00bb" );
            parser.defineEntityReplacementText( "frac14", "\u00bc" );
            parser.defineEntityReplacementText( "frac12", "\u00bd" );
            parser.defineEntityReplacementText( "frac34", "\u00be" );
            parser.defineEntityReplacementText( "iquest", "\u00bf" );
            parser.defineEntityReplacementText( "Agrave", "\u00c0" );
            parser.defineEntityReplacementText( "Aacute", "\u00c1" );
            parser.defineEntityReplacementText( "Acirc", "\u00c2" );
            parser.defineEntityReplacementText( "Atilde", "\u00c3" );
            parser.defineEntityReplacementText( "Auml", "\u00c4" );
            parser.defineEntityReplacementText( "Aring", "\u00c5" );
            parser.defineEntityReplacementText( "AElig", "\u00c6" );
            parser.defineEntityReplacementText( "Ccedil", "\u00c7" );
            parser.defineEntityReplacementText( "Egrave", "\u00c8" );
            parser.defineEntityReplacementText( "Eacute", "\u00c9" );
            parser.defineEntityReplacementText( "Ecirc", "\u00ca" );
            parser.defineEntityReplacementText( "Euml", "\u00cb" );
            parser.defineEntityReplacementText( "Igrave", "\u00cc" );
            parser.defineEntityReplacementText( "Iacute", "\u00cd" );
            parser.defineEntityReplacementText( "Icirc", "\u00ce" );
            parser.defineEntityReplacementText( "Iuml", "\u00cf" );
            parser.defineEntityReplacementText( "ETH", "\u00d0" );
            parser.defineEntityReplacementText( "Ntilde", "\u00d1" );
            parser.defineEntityReplacementText( "Ograve", "\u00d2" );
            parser.defineEntityReplacementText( "Oacute", "\u00d3" );
            parser.defineEntityReplacementText( "Ocirc", "\u00d4" );
            parser.defineEntityReplacementText( "Otilde", "\u00d5" );
            parser.defineEntityReplacementText( "Ouml", "\u00d6" );
            parser.defineEntityReplacementText( "times", "\u00d7" );
            parser.defineEntityReplacementText( "Oslash", "\u00d8" );
            parser.defineEntityReplacementText( "Ugrave", "\u00d9" );
            parser.defineEntityReplacementText( "Uacute", "\u00da" );
            parser.defineEntityReplacementText( "Ucirc", "\u00db" );
            parser.defineEntityReplacementText( "Uuml", "\u00dc" );
            parser.defineEntityReplacementText( "Yacute", "\u00dd" );
            parser.defineEntityReplacementText( "THORN", "\u00de" );
            parser.defineEntityReplacementText( "szlig", "\u00df" );
            parser.defineEntityReplacementText( "agrave", "\u00e0" );
            parser.defineEntityReplacementText( "aacute", "\u00e1" );
            parser.defineEntityReplacementText( "acirc", "\u00e2" );
            parser.defineEntityReplacementText( "atilde", "\u00e3" );
            parser.defineEntityReplacementText( "auml", "\u00e4" );
            parser.defineEntityReplacementText( "aring", "\u00e5" );
            parser.defineEntityReplacementText( "aelig", "\u00e6" );
            parser.defineEntityReplacementText( "ccedil", "\u00e7" );
            parser.defineEntityReplacementText( "egrave", "\u00e8" );
            parser.defineEntityReplacementText( "eacute", "\u00e9" );
            parser.defineEntityReplacementText( "ecirc", "\u00ea" );
            parser.defineEntityReplacementText( "euml", "\u00eb" );
            parser.defineEntityReplacementText( "igrave", "\u00ec" );
            parser.defineEntityReplacementText( "iacute", "\u00ed" );
            parser.defineEntityReplacementText( "icirc", "\u00ee" );
            parser.defineEntityReplacementText( "iuml", "\u00ef" );
            parser.defineEntityReplacementText( "eth", "\u00f0" );
            parser.defineEntityReplacementText( "ntilde", "\u00f1" );
            parser.defineEntityReplacementText( "ograve", "\u00f2" );
            parser.defineEntityReplacementText( "oacute", "\u00f3" );
            parser.defineEntityReplacementText( "ocirc", "\u00f4" );
            parser.defineEntityReplacementText( "otilde", "\u00f5" );
            parser.defineEntityReplacementText( "ouml", "\u00f6" );
            parser.defineEntityReplacementText( "divide", "\u00f7" );
            parser.defineEntityReplacementText( "oslash", "\u00f8" );
            parser.defineEntityReplacementText( "ugrave", "\u00f9" );
            parser.defineEntityReplacementText( "uacute", "\u00fa" );
            parser.defineEntityReplacementText( "ucirc", "\u00fb" );
            parser.defineEntityReplacementText( "uuml", "\u00fc" );
            parser.defineEntityReplacementText( "yacute", "\u00fd" );
            parser.defineEntityReplacementText( "thorn", "\u00fe" );
            parser.defineEntityReplacementText( "yuml", "\u00ff" );
    
            // ----------------------------------------------------------------------
            // Special entities
            // ----------------------------------------------------------------------
    
            parser.defineEntityReplacementText( "OElig", "\u0152" );
            parser.defineEntityReplacementText( "oelig", "\u0153" );
            parser.defineEntityReplacementText( "Scaron", "\u0160" );
            parser.defineEntityReplacementText( "scaron", "\u0161" );
            parser.defineEntityReplacementText( "Yuml", "\u0178" );
            parser.defineEntityReplacementText( "circ", "\u02c6" );
            parser.defineEntityReplacementText( "tilde", "\u02dc" );
            parser.defineEntityReplacementText( "ensp", "\u2002" );
            parser.defineEntityReplacementText( "emsp", "\u2003" );
            parser.defineEntityReplacementText( "thinsp", "\u2009" );
            parser.defineEntityReplacementText( "zwnj", "\u200c" );
            parser.defineEntityReplacementText( "zwj", "\u200d" );
            parser.defineEntityReplacementText( "lrm", "\u200e" );
            parser.defineEntityReplacementText( "rlm", "\u200f" );
            parser.defineEntityReplacementText( "ndash", "\u2013" );
            parser.defineEntityReplacementText( "mdash", "\u2014" );
            parser.defineEntityReplacementText( "lsquo", "\u2018" );
            parser.defineEntityReplacementText( "rsquo", "\u2019" );
            parser.defineEntityReplacementText( "sbquo", "\u201a" );
            parser.defineEntityReplacementText( "ldquo", "\u201c" );
            parser.defineEntityReplacementText( "rdquo", "\u201d" );
            parser.defineEntityReplacementText( "bdquo", "\u201e" );
            parser.defineEntityReplacementText( "dagger", "\u2020" );
            parser.defineEntityReplacementText( "Dagger", "\u2021" );
            parser.defineEntityReplacementText( "permil", "\u2030" );
            parser.defineEntityReplacementText( "lsaquo", "\u2039" );
            parser.defineEntityReplacementText( "rsaquo", "\u203a" );
            parser.defineEntityReplacementText( "euro", "\u20ac" );
    
            // ----------------------------------------------------------------------
            // Symbol entities
            // ----------------------------------------------------------------------
    
            parser.defineEntityReplacementText( "fnof", "\u0192" );
            parser.defineEntityReplacementText( "Alpha", "\u0391" );
            parser.defineEntityReplacementText( "Beta", "\u0392" );
            parser.defineEntityReplacementText( "Gamma", "\u0393" );
            parser.defineEntityReplacementText( "Delta", "\u0394" );
            parser.defineEntityReplacementText( "Epsilon", "\u0395" );
            parser.defineEntityReplacementText( "Zeta", "\u0396" );
            parser.defineEntityReplacementText( "Eta", "\u0397" );
            parser.defineEntityReplacementText( "Theta", "\u0398" );
            parser.defineEntityReplacementText( "Iota", "\u0399" );
            parser.defineEntityReplacementText( "Kappa", "\u039a" );
            parser.defineEntityReplacementText( "Lambda", "\u039b" );
            parser.defineEntityReplacementText( "Mu", "\u039c" );
            parser.defineEntityReplacementText( "Nu", "\u039d" );
            parser.defineEntityReplacementText( "Xi", "\u039e" );
            parser.defineEntityReplacementText( "Omicron", "\u039f" );
            parser.defineEntityReplacementText( "Pi", "\u03a0" );
            parser.defineEntityReplacementText( "Rho", "\u03a1" );
            parser.defineEntityReplacementText( "Sigma", "\u03a3" );
            parser.defineEntityReplacementText( "Tau", "\u03a4" );
            parser.defineEntityReplacementText( "Upsilon", "\u03a5" );
            parser.defineEntityReplacementText( "Phi", "\u03a6" );
            parser.defineEntityReplacementText( "Chi", "\u03a7" );
            parser.defineEntityReplacementText( "Psi", "\u03a8" );
            parser.defineEntityReplacementText( "Omega", "\u03a9" );
            parser.defineEntityReplacementText( "alpha", "\u03b1" );
            parser.defineEntityReplacementText( "beta", "\u03b2" );
            parser.defineEntityReplacementText( "gamma", "\u03b3" );
            parser.defineEntityReplacementText( "delta", "\u03b4" );
            parser.defineEntityReplacementText( "epsilon", "\u03b5" );
            parser.defineEntityReplacementText( "zeta", "\u03b6" );
            parser.defineEntityReplacementText( "eta", "\u03b7" );
            parser.defineEntityReplacementText( "theta", "\u03b8" );
            parser.defineEntityReplacementText( "iota", "\u03b9" );
            parser.defineEntityReplacementText( "kappa", "\u03ba" );
            parser.defineEntityReplacementText( "lambda", "\u03bb" );
            parser.defineEntityReplacementText( "mu", "\u03bc" );
            parser.defineEntityReplacementText( "nu", "\u03bd" );
            parser.defineEntityReplacementText( "xi", "\u03be" );
            parser.defineEntityReplacementText( "omicron", "\u03bf" );
            parser.defineEntityReplacementText( "pi", "\u03c0" );
            parser.defineEntityReplacementText( "rho", "\u03c1" );
            parser.defineEntityReplacementText( "sigmaf", "\u03c2" );
            parser.defineEntityReplacementText( "sigma", "\u03c3" );
            parser.defineEntityReplacementText( "tau", "\u03c4" );
            parser.defineEntityReplacementText( "upsilon", "\u03c5" );
            parser.defineEntityReplacementText( "phi", "\u03c6" );
            parser.defineEntityReplacementText( "chi", "\u03c7" );
            parser.defineEntityReplacementText( "psi", "\u03c8" );
            parser.defineEntityReplacementText( "omega", "\u03c9" );
            parser.defineEntityReplacementText( "thetasym", "\u03d1" );
            parser.defineEntityReplacementText( "upsih", "\u03d2" );
            parser.defineEntityReplacementText( "piv", "\u03d6" );
            parser.defineEntityReplacementText( "bull", "\u2022" );
            parser.defineEntityReplacementText( "hellip", "\u2026" );
            parser.defineEntityReplacementText( "prime", "\u2032" );
            parser.defineEntityReplacementText( "Prime", "\u2033" );
            parser.defineEntityReplacementText( "oline", "\u203e" );
            parser.defineEntityReplacementText( "frasl", "\u2044" );
            parser.defineEntityReplacementText( "weierp", "\u2118" );
            parser.defineEntityReplacementText( "image", "\u2111" );
            parser.defineEntityReplacementText( "real", "\u211c" );
            parser.defineEntityReplacementText( "trade", "\u2122" );
            parser.defineEntityReplacementText( "alefsym", "\u2135" );
            parser.defineEntityReplacementText( "larr", "\u2190" );
            parser.defineEntityReplacementText( "uarr", "\u2191" );
            parser.defineEntityReplacementText( "rarr", "\u2192" );
            parser.defineEntityReplacementText( "darr", "\u2193" );
            parser.defineEntityReplacementText( "harr", "\u2194" );
            parser.defineEntityReplacementText( "crarr", "\u21b5" );
            parser.defineEntityReplacementText( "lArr", "\u21d0" );
            parser.defineEntityReplacementText( "uArr", "\u21d1" );
            parser.defineEntityReplacementText( "rArr", "\u21d2" );
            parser.defineEntityReplacementText( "dArr", "\u21d3" );
            parser.defineEntityReplacementText( "hArr", "\u21d4" );
            parser.defineEntityReplacementText( "forall", "\u2200" );
            parser.defineEntityReplacementText( "part", "\u2202" );
            parser.defineEntityReplacementText( "exist", "\u2203" );
            parser.defineEntityReplacementText( "empty", "\u2205" );
            parser.defineEntityReplacementText( "nabla", "\u2207" );
            parser.defineEntityReplacementText( "isin", "\u2208" );
            parser.defineEntityReplacementText( "notin", "\u2209" );
            parser.defineEntityReplacementText( "ni", "\u220b" );
            parser.defineEntityReplacementText( "prod", "\u220f" );
            parser.defineEntityReplacementText( "sum", "\u2211" );
            parser.defineEntityReplacementText( "minus", "\u2212" );
            parser.defineEntityReplacementText( "lowast", "\u2217" );
            parser.defineEntityReplacementText( "radic", "\u221a" );
            parser.defineEntityReplacementText( "prop", "\u221d" );
            parser.defineEntityReplacementText( "infin", "\u221e" );
            parser.defineEntityReplacementText( "ang", "\u2220" );
            parser.defineEntityReplacementText( "and", "\u2227" );
            parser.defineEntityReplacementText( "or", "\u2228" );
            parser.defineEntityReplacementText( "cap", "\u2229" );
            parser.defineEntityReplacementText( "cup", "\u222a" );
            parser.defineEntityReplacementText( "int", "\u222b" );
            parser.defineEntityReplacementText( "there4", "\u2234" );
            parser.defineEntityReplacementText( "sim", "\u223c" );
            parser.defineEntityReplacementText( "cong", "\u2245" );
            parser.defineEntityReplacementText( "asymp", "\u2248" );
            parser.defineEntityReplacementText( "ne", "\u2260" );
            parser.defineEntityReplacementText( "equiv", "\u2261" );
            parser.defineEntityReplacementText( "le", "\u2264" );
            parser.defineEntityReplacementText( "ge", "\u2265" );
            parser.defineEntityReplacementText( "sub", "\u2282" );
            parser.defineEntityReplacementText( "sup", "\u2283" );
            parser.defineEntityReplacementText( "nsub", "\u2284" );
            parser.defineEntityReplacementText( "sube", "\u2286" );
            parser.defineEntityReplacementText( "supe", "\u2287" );
            parser.defineEntityReplacementText( "oplus", "\u2295" );
            parser.defineEntityReplacementText( "otimes", "\u2297" );
            parser.defineEntityReplacementText( "perp", "\u22a5" );
            parser.defineEntityReplacementText( "sdot", "\u22c5" );
            parser.defineEntityReplacementText( "lceil", "\u2308" );
            parser.defineEntityReplacementText( "rceil", "\u2309" );
            parser.defineEntityReplacementText( "lfloor", "\u230a" );
            parser.defineEntityReplacementText( "rfloor", "\u230b" );
            parser.defineEntityReplacementText( "lang", "\u2329" );
            parser.defineEntityReplacementText( "rang", "\u232a" );
            parser.defineEntityReplacementText( "loz", "\u25ca" );
            parser.defineEntityReplacementText( "spades", "\u2660" );
            parser.defineEntityReplacementText( "clubs", "\u2663" );
            parser.defineEntityReplacementText( "hearts", "\u2665" );
            parser.defineEntityReplacementText( "diams", "\u2666" );
    
        }
    
        parser.next();
        return parseModel( "project", parser, strict );
    } //-- Model read( Reader, boolean ) 

    /**
     * @see ReaderFactory#newXmlReader
     * 
     * @param reader
     * @throws IOException
     * @throws XmlPullParserException
     * @return Model
     */
    public Model read( Reader reader )
        throws IOException, XmlPullParserException
    {
        return read( reader, true );
    } //-- Model read( Reader ) 

}
