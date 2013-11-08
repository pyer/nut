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
//import ab.nut.model.DependencyManagement;
//import ab.nut.model.Exclusion;
//import ab.nut.model.Extension;
//import ab.nut.model.FileSet;
import nut.model.Model;
//import ab.nut.model.PatternSet;
import nut.model.Plugin;
import nut.model.PluginContainer;
//import ab.nut.model.Resource;
//import ab.nut.model.Scm;
//import ab.nut.model.UnitTest;

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

            if ( build.getPlugins() != null && build.getPlugins().size() > 0 )
            {
                serializer.startTag( NAMESPACE, "plugins" );
                for ( Iterator iter = build.getPlugins().iterator(); iter.hasNext(); )
                {
                    Plugin o = (Plugin) iter.next();
                    writePlugin( o, "plugin", serializer );
                }
                serializer.endTag( NAMESPACE, "plugins" );
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

    /**
     * Method writePlugin.
     * 
     * @param plugin
     * @param serializer
     * @param tagName
     * @throws java.io.IOException
     */
    private void writePlugin( Plugin plugin, String tagName, XmlSerializer serializer )
        throws java.io.IOException
    {
        if ( plugin != null )
        {
            serializer.startTag( NAMESPACE, tagName );
            if ( plugin.getGroupId() != null )
            {
                serializer.startTag( NAMESPACE, "groupId" ).text( plugin.getGroupId() ).endTag( NAMESPACE, "groupId" );
            }
            if ( plugin.getArtifactId() != null )
            {
                serializer.startTag( NAMESPACE, "artifactId" ).text( plugin.getArtifactId() ).endTag( NAMESPACE, "artifactId" );
            }
            if ( plugin.getVersion() != null )
            {
                serializer.startTag( NAMESPACE, "version" ).text( plugin.getVersion() ).endTag( NAMESPACE, "version" );
            }
            if ( plugin.getGoal() != null )
            {
                serializer.startTag( NAMESPACE, "goal" ).text( plugin.getGoal() ).endTag( NAMESPACE, "goal" );
            }
            if ( plugin.getSkip() )
            {
                serializer.startTag( NAMESPACE, "skip" ).text( "true" ).endTag( NAMESPACE, "skip" );
            }
            serializer.endTag( NAMESPACE, tagName );
        }
    } //-- void writePlugin( Plugin, String, XmlSerializer ) 

    /**
     * Method writePluginContainer.
     * 
     * @param pluginContainer
     * @param serializer
     * @param tagName
     * @throws java.io.IOException
     */
    private void writePluginContainer( PluginContainer pluginContainer, String tagName, XmlSerializer serializer )
        throws java.io.IOException
    {
        if ( pluginContainer != null )
        {
            serializer.startTag( NAMESPACE, tagName );
            if ( pluginContainer.getPlugins() != null && pluginContainer.getPlugins().size() > 0 )
            {
                serializer.startTag( NAMESPACE, "plugins" );
                for ( Iterator iter = pluginContainer.getPlugins().iterator(); iter.hasNext(); )
                {
                    Plugin o = (Plugin) iter.next();
                    writePlugin( o, "plugin", serializer );
                }
                serializer.endTag( NAMESPACE, "plugins" );
            }
            serializer.endTag( NAMESPACE, tagName );
        }
    } //-- void writePluginContainer( PluginContainer, String, XmlSerializer ) 

}
