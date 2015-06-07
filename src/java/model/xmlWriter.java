package nut.model;

import java.io.Writer;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.Locale;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Goal;
import nut.model.Model;

import nut.xml.XmlSerializer;

public class xmlWriter {

    /**
     * Field NAMESPACE.
     */
    private String NAMESPACE;


    //-----------/
    //- Methods -/
    //-----------/

    /**
     * Method writeTag.
     * 
     * @param tag
     * @param value
     * @param serializer
     * @throws java.io.IOException
     */
    private void writeTag( String tag, String value, XmlSerializer serializer )
        throws java.io.IOException
    {
        if ( value != null ) {
          serializer.startTag( NAMESPACE, tag ).text( value ).endTag( NAMESPACE, tag );
        }
    }

    /**
     * Method writeGoal.
     * 
     * @param goal
     * @param tagName
     * @param serializer
     * @throws java.io.IOException
     */
    private void writeGoal( Goal goal, String tagName, XmlSerializer serializer )
        throws java.io.IOException
    {
        if ( goal != null ) {
            serializer.startTag( NAMESPACE, tagName );
            writeTag( "name", goal.getName(), serializer );
            writeTag( "type", goal.getType(), serializer );
            if ( goal.hasConfiguration() ) {
                serializer.startTag( NAMESPACE, "configuration" );
                for ( Iterator iter = goal.configuration().keySet().iterator(); iter.hasNext(); ) {
                    String key = (String) iter.next();
                    String value = (String) goal.getConfigurationValue( key );
                    writeTag( "" + key + "", value, serializer );
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
     * @param tagName
     * @param serializer
     * @throws java.io.IOException
     */
    private void writeBuild( Build build, String tagName, XmlSerializer serializer )
        throws java.io.IOException
    {
        if ( build != null ) {
            serializer.startTag( NAMESPACE, tagName );
            writeTag( "sourceDirectory", build.getSourceDirectory(), serializer );
            writeTag( "resourceDirectory", build.getResourceDirectory(), serializer );
            writeTag( "testSourceDirectory", build.getTestSourceDirectory(), serializer );
            writeTag( "testResourceDirectory", build.getTestResourceDirectory(), serializer );
            writeTag( "targetDirectory", build.getTargetDirectory(), serializer );
            writeTag( "outputDirectory", build.getOutputDirectory(), serializer );
            writeTag( "testOutputDirectory", build.getTestOutputDirectory(), serializer );
            writeTag( "testReportDirectory", build.getTestReportDirectory(), serializer );
            if ( build.getGoals() != null && build.getGoals().size() > 0 ) {
                serializer.startTag( NAMESPACE, "goals" );
                for ( Iterator iter = build.getGoals().iterator(); iter.hasNext(); ) {
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
     * @param tagName
     * @param serializer
     * @throws java.io.IOException
     */
    private void writeDependency( Dependency dependency, String tagName, XmlSerializer serializer )
        throws java.io.IOException
    {
        if ( dependency != null ) {
            serializer.startTag( NAMESPACE, tagName );
            writeTag( "groupeId", dependency.getGroupId(), serializer );
            writeTag( "artifactId", dependency.getArtifactId(), serializer );
            writeTag( "version", dependency.getVersion(), serializer );
            writeTag( "type", dependency.getType(), serializer );
            writeTag( "scope", dependency.getScope(), serializer );
            serializer.endTag( NAMESPACE, tagName );
        }
    } //-- void writeDependency( Dependency, String, XmlSerializer ) 

    /**
     * Method writeModel.
     * 
     * @param sWriter
     * @param model
     * @throws java.io.IOException
     */
    public void writeModel( StringWriter sWriter, Model model )
        throws java.io.IOException
    {
        String tagName = "project";
        XmlSerializer serializer = new XmlSerializer();
        serializer.setProperty( "http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  " );
        serializer.setProperty( "http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n" );
        serializer.setOutput( sWriter );
        serializer.startDocument( model.getModelEncoding(), null );
        if ( model != null ) {
            serializer.startTag( NAMESPACE, tagName );
            writeTag( "modelVersion", model.getModelVersion(), serializer );
            writeTag( "groupeId", model.getGroupId(), serializer );
            writeTag( "artifactId", model.getArtifactId(), serializer );
            writeTag( "version", model.getVersion(), serializer );
            writeTag( "packaging", model.getPackaging(), serializer );
            writeTag( "name", model.getName(), serializer );
            writeTag( "description", model.getDescription(), serializer );
            if ( model.getBuild() != null ) {
                writeBuild( (Build) model.getBuild(), "build", serializer );
            }
            if ( model.getModules() != null && model.getModules().size() > 0 ) {
                serializer.startTag( NAMESPACE, "modules" );
                for ( Iterator iter = model.getModules().iterator(); iter.hasNext(); ) {
                    String module = (String) iter.next();
                    writeTag( "module", module, serializer );
                }
                serializer.endTag( NAMESPACE, "modules" );
            }
            if ( model.getDependencies() != null && model.getDependencies().size() > 0 ) {
                serializer.startTag( NAMESPACE, "dependencies" );
                for ( Iterator iter = model.getDependencies().iterator(); iter.hasNext(); ) {
                    Dependency o = (Dependency) iter.next();
                    writeDependency( o, "dependency", serializer );
                }
                serializer.endTag( NAMESPACE, "dependencies" );
            }
            if ( model.getProperties() != null && model.getProperties().size() > 0 ) {
                serializer.startTag( NAMESPACE, "properties" );
                for ( Iterator iter = model.getProperties().keySet().iterator(); iter.hasNext(); ) {
                    String key = (String) iter.next();
                    String value = (String) model.getProperties().get( key );
                    writeTag( "" + key + "", value, serializer );
                }
                serializer.endTag( NAMESPACE, "properties" );
            }
            serializer.endTag( NAMESPACE, tagName );
        }
        serializer.endDocument();
    }

}
