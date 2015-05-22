package nut.model;

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/

import java.io.Writer;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.Locale;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Goal;
import nut.model.Model;

import nut.xml.XmlSerializer;

/**
 * Class modelXpp3Writer.
 * 
 * @version $Revision$ $Date$
 */
public class xmlWriter {


    //--------------------------/
    //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field NAMESPACE.
     */
    private String NAMESPACE;


    //-----------/
    //- Methods -/
    //-----------/

    /**
     * Method write.
     * 
     * @param writer
     * @param model
     * @throws java.io.IOException
     */
    public void write( Writer writer, Model model )
        throws java.io.IOException
    {
        XmlSerializer serializer = new XmlSerializer();
        serializer.setProperty( "http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  " );
        serializer.setProperty( "http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n" );
        serializer.setOutput( writer );
        serializer.startDocument( model.getModelEncoding(), null );
        writeModel( model, "project", serializer );
        serializer.endDocument();
    } //-- void write( Writer, Model ) 

    /**
     * Method writeGoal.
     * 
     * @param goal
     * @param serializer
     * @param tagName
     * @throws java.io.IOException
     */
    private void writeGoal( Goal goal, String tagName, XmlSerializer serializer )
        throws java.io.IOException
    {
        if ( goal != null )
        {
            serializer.startTag( NAMESPACE, tagName );
            if ( goal.getName() != null )
            {
                serializer.startTag( NAMESPACE, "name" ).text( goal.getName() ).endTag( NAMESPACE, "name" );
            }
            if ( goal.getType() != null )
            {
                serializer.startTag( NAMESPACE, "type" ).text( goal.getType() ).endTag( NAMESPACE, "type" );
            }
            if ( goal.getConfiguration() != null && goal.getConfiguration().size() > 0 )
            {
                serializer.startTag( NAMESPACE, "configuration" );
                for ( Iterator iter = goal.getConfiguration().keySet().iterator(); iter.hasNext(); )
                {
                    String key = (String) iter.next();
                    String value = (String) goal.getConfigurationValue( key );
                    serializer.startTag( NAMESPACE, "" + key + "" ).text( value ).endTag( NAMESPACE, "" + key + "" );
                }
                serializer.endTag( NAMESPACE, "configuration" );
            }
            serializer.endTag( NAMESPACE, tagName );
        }
    } //-- void writeGoal( Goal, String, XmlSerializer ) 

    /**
     * Method writeBuild.
     * 
     * @param build
     * @param serializer
     * @param tagName
     * @throws java.io.IOException
     */
    private void writeBuild( Build build, String tagName, XmlSerializer serializer )
        throws java.io.IOException
    {
        if ( build != null )
        {
            serializer.startTag( NAMESPACE, tagName );
            if ( build.getDirectory() != null )
            {
                serializer.startTag( NAMESPACE, "directory" ).text( build.getDirectory() ).endTag( NAMESPACE, "directory" );
            }
            if ( build.getOutputDirectory() != null )
            {
                serializer.startTag( NAMESPACE, "outputDirectory" ).text( build.getOutputDirectory() ).endTag( NAMESPACE, "outputDirectory" );
            }
            if ( build.getTestOutputDirectory() != null )
            {
                serializer.startTag( NAMESPACE, "testOutputDirectory" ).text( build.getTestOutputDirectory() ).endTag( NAMESPACE, "testOutputDirectory" );
            }
            if ( build.getSourceDirectory() != null )
            {
                serializer.startTag( NAMESPACE, "sourceDirectory" ).text( build.getSourceDirectory() ).endTag( NAMESPACE, "sourceDirectory" );
            }
            if ( build.getTestSourceDirectory() != null )
            {
                serializer.startTag( NAMESPACE, "testSourceDirectory" ).text( build.getTestSourceDirectory() ).endTag( NAMESPACE, "testSourceDirectory" );
            }

            if ( build.getGoals() != null && build.getGoals().size() > 0 )
            {
                serializer.startTag( NAMESPACE, "goals" );
                for ( Iterator iter = build.getGoals().iterator(); iter.hasNext(); )
                {
                    Goal o = (Goal) iter.next();
                    writeGoal( o, "goal", serializer );
                }
                serializer.endTag( NAMESPACE, "goals" );
            }
            serializer.endTag( NAMESPACE, tagName );
        }
    } //-- void writeBuild( Build, String, XmlSerializer ) 

    /**
     * Method writeDependency.
     * 
     * @param dependency
     * @param serializer
     * @param tagName
     * @throws java.io.IOException
     */
    private void writeDependency( Dependency dependency, String tagName, XmlSerializer serializer )
        throws java.io.IOException
    {
        if ( dependency != null )
        {
            serializer.startTag( NAMESPACE, tagName );
            if ( dependency.getGroupId() != null )
            {
                serializer.startTag( NAMESPACE, "groupId" ).text( dependency.getGroupId() ).endTag( NAMESPACE, "groupId" );
            }
            if ( dependency.getArtifactId() != null )
            {
                serializer.startTag( NAMESPACE, "artifactId" ).text( dependency.getArtifactId() ).endTag( NAMESPACE, "artifactId" );
            }
            if ( dependency.getVersion() != null )
            {
                serializer.startTag( NAMESPACE, "version" ).text( dependency.getVersion() ).endTag( NAMESPACE, "version" );
            }
            if ( dependency.getType() != null )
            {
                serializer.startTag( NAMESPACE, "type" ).text( dependency.getType() ).endTag( NAMESPACE, "type" );
            }
            if ( dependency.getClassifier() != null )
            {
                serializer.startTag( NAMESPACE, "classifier" ).text( dependency.getClassifier() ).endTag( NAMESPACE, "classifier" );
            }
            if ( dependency.getScope() != null )
            {
                serializer.startTag( NAMESPACE, "scope" ).text( dependency.getScope() ).endTag( NAMESPACE, "scope" );
            }
            if ( dependency.getProperties() != null && dependency.getProperties().size() > 0 )
            {
                serializer.startTag( NAMESPACE, "properties" );
                for ( Iterator iter = dependency.getProperties().keySet().iterator(); iter.hasNext(); )
                {
                    String key = (String) iter.next();
                    String value = (String) dependency.getProperties().get( key );
                    serializer.startTag( NAMESPACE, "" + key + "" ).text( value ).endTag( NAMESPACE, "" + key + "" );
                }
                serializer.endTag( NAMESPACE, "properties" );
            }
            serializer.endTag( NAMESPACE, tagName );
        }
    } //-- void writeDependency( Dependency, String, XmlSerializer ) 


    /**
     * Method writeModel.
     * 
     * @param model
     * @param serializer
     * @param tagName
     * @throws java.io.IOException
     */
    private void writeModel( Model model, String tagName, XmlSerializer serializer )
        throws java.io.IOException
    {
        if ( model != null )
        {
            serializer.startTag( NAMESPACE, tagName );
            if ( model.getModelVersion() != null )
            {
                serializer.startTag( NAMESPACE, "modelVersion" ).text( model.getModelVersion() ).endTag( NAMESPACE, "modelVersion" );
            }
            if ( model.getGroupId() != null )
            {
                serializer.startTag( NAMESPACE, "groupId" ).text( model.getGroupId() ).endTag( NAMESPACE, "groupId" );
            }
            if ( model.getArtifactId() != null )
            {
                serializer.startTag( NAMESPACE, "artifactId" ).text( model.getArtifactId() ).endTag( NAMESPACE, "artifactId" );
            }
            if ( model.getVersion() != null )
            {
                serializer.startTag( NAMESPACE, "version" ).text( model.getVersion() ).endTag( NAMESPACE, "version" );
            }
            if ( model.getPackaging() != null )
            {
                serializer.startTag( NAMESPACE, "packaging" ).text( model.getPackaging() ).endTag( NAMESPACE, "packaging" );
            }
            if ( model.getName() != null )
            {
                serializer.startTag( NAMESPACE, "name" ).text( model.getName() ).endTag( NAMESPACE, "name" );
            }
            if ( model.getDescription() != null )
            {
                serializer.startTag( NAMESPACE, "description" ).text( model.getDescription() ).endTag( NAMESPACE, "description" );
            }
            if ( model.getUrl() != null )
            {
                serializer.startTag( NAMESPACE, "url" ).text( model.getUrl() ).endTag( NAMESPACE, "url" );
            }
            if ( model.getBuild() != null )
            {
                writeBuild( (Build) model.getBuild(), "build", serializer );
            }
            if ( model.getModules() != null && model.getModules().size() > 0 )
            {
                serializer.startTag( NAMESPACE, "modules" );
                for ( Iterator iter = model.getModules().iterator(); iter.hasNext(); )
                {
                    String module = (String) iter.next();
                    serializer.startTag( NAMESPACE, "module" ).text( module ).endTag( NAMESPACE, "module" );
                }
                serializer.endTag( NAMESPACE, "modules" );
            }
            if ( model.getDependencies() != null && model.getDependencies().size() > 0 )
            {
                serializer.startTag( NAMESPACE, "dependencies" );
                for ( Iterator iter = model.getDependencies().iterator(); iter.hasNext(); )
                {
                    Dependency o = (Dependency) iter.next();
                    writeDependency( o, "dependency", serializer );
                }
                serializer.endTag( NAMESPACE, "dependencies" );
            }
            if ( model.getProperties() != null && model.getProperties().size() > 0 )
            {
                serializer.startTag( NAMESPACE, "properties" );
                for ( Iterator iter = model.getProperties().keySet().iterator(); iter.hasNext(); )
                {
                    String key = (String) iter.next();
                    String value = (String) model.getProperties().get( key );
                    serializer.startTag( NAMESPACE, "" + key + "" ).text( value ).endTag( NAMESPACE, "" + key + "" );
                }
                serializer.endTag( NAMESPACE, "properties" );
            }
            serializer.endTag( NAMESPACE, tagName );
        }
    } //-- void writeModel( Model, String, XmlSerializer ) 

}
