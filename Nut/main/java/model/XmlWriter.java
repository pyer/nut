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

public class XmlWriter {

    /**
     * Method writeElement.
     * 
     * @param tag
     * @param value
     * @param serializer
     * @throws java.io.IOException
     */
    private void writeElement( String tag, String value, XmlSerializer serializer )
        throws java.io.IOException
    {
        if ( value != null ) {
          serializer.startTag( tag ).text( value ).endTag( tag );
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
            serializer.startTag( tagName );
            writeElement( "name", goal.getName(), serializer );
            writeElement( "type", goal.getType(), serializer );
            if ( goal.hasConfiguration() ) {
                serializer.startTag( "configuration" );
                for ( Iterator iter = goal.configuration().keySet().iterator(); iter.hasNext(); ) {
                    String key = (String) iter.next();
                    String value = (String) goal.getConfigurationValue( key );
                    writeElement( "" + key + "", value, serializer );
                }
                serializer.endTag( "configuration" );
            }
            serializer.endTag( tagName );
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
            serializer.startTag( tagName );
            writeElement( "sourceDirectory", build.getSourceDirectory(), serializer );
            writeElement( "resourceDirectory", build.getResourceDirectory(), serializer );
            writeElement( "testSourceDirectory", build.getTestSourceDirectory(), serializer );
            writeElement( "testResourceDirectory", build.getTestResourceDirectory(), serializer );
            writeElement( "targetDirectory", build.getTargetDirectory(), serializer );
            writeElement( "outputDirectory", build.getOutputDirectory(), serializer );
            writeElement( "testOutputDirectory", build.getTestOutputDirectory(), serializer );
            writeElement( "testReportDirectory", build.getTestReportDirectory(), serializer );
            if ( build.getGoals() != null && build.getGoals().size() > 0 ) {
                serializer.startTag( "goals" );
                for ( Iterator iter = build.getGoals().iterator(); iter.hasNext(); ) {
                    Goal o = (Goal) iter.next();
                    writeGoal( o, "goal", serializer );
                }
                serializer.endTag( "goals" );
            }
            serializer.endTag( tagName );
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
            serializer.startTag( tagName );
            writeElement( "groupId", dependency.getGroupId(), serializer );
            writeElement( "artifactId", dependency.getArtifactId(), serializer );
            writeElement( "version", dependency.getVersion(), serializer );
            writeElement( "type", dependency.getType(), serializer );
            writeElement( "scope", dependency.getScope(), serializer );
            serializer.endTag( tagName );
        }
    } //-- void writeDependency( Dependency, String, XmlSerializer ) 

    /**
     * Method writeRepository.
     * 
     * @param repository
     * @param tagName
     * @param serializer
     * @throws java.io.IOException
     */
    private void writeRepository( Repository repository, String tagName, XmlSerializer serializer )
        throws java.io.IOException
    {
        if ( repository != null ) {
            serializer.startTag( tagName );
            writeElement( "name", repository.getName(), serializer );
            writeElement( "layout", repository.getLayout(), serializer );
            writeElement( "url", repository.getURL(), serializer );
            serializer.endTag( tagName );
        }
    }

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
        serializer.setOutput( sWriter );
        if ( model == null ) {
            // default encoding
            serializer.startDocument( "UTF-8" );
        } else {
            serializer.startDocument( model.getModelEncoding() );
            serializer.startTag( tagName );
            writeElement( "modelVersion", model.getModelVersion(), serializer );
            writeElement( "modelEncoding", model.getModelEncoding(), serializer );
            writeElement( "parent", model.getParent(), serializer );
            writeElement( "groupId", model.getGroupId(), serializer );
            writeElement( "artifactId", model.getArtifactId(), serializer );
            writeElement( "version", model.getVersion(), serializer );
            writeElement( "packaging", model.getPackaging(), serializer );
            writeElement( "name", model.getName(), serializer );
            writeElement( "description", model.getDescription(), serializer );
            if ( model.getBuild() != null ) {
                writeBuild( (Build) model.getBuild(), "build", serializer );
            }
            if ( model.getModules() != null && model.getModules().size() > 0 ) {
                serializer.startTag( "modules" );
                for ( Iterator iter = model.getModules().iterator(); iter.hasNext(); ) {
                    String module = (String) iter.next();
                    writeElement( "module", module, serializer );
                }
                serializer.endTag( "modules" );
            }
            if ( model.getDependencies() != null && model.getDependencies().size() > 0 ) {
                serializer.startTag( "dependencies" );
                for ( Iterator iter = model.getDependencies().iterator(); iter.hasNext(); ) {
                    Dependency o = (Dependency) iter.next();
                    writeDependency( o, "dependency", serializer );
                }
                serializer.endTag( "dependencies" );
            }
            if ( model.getRepositories() != null && model.getRepositories().size() > 0 ) {
                serializer.startTag( "repositories" );
                for ( Iterator iter = model.getRepositories().iterator(); iter.hasNext(); ) {
                    Repository o = (Repository) iter.next();
                    writeRepository( o, "repository", serializer );
                }
                serializer.endTag( "repositories" );
            }
            if ( model.getProperties() != null && model.getProperties().size() > 0 ) {
                serializer.startTag( "properties" );
                for ( Iterator iter = model.getProperties().keySet().iterator(); iter.hasNext(); ) {
                    String key = (String) iter.next();
                    String value = (String) model.getProperties().get( key );
                    writeElement( "" + key + "", value, serializer );
                }
                serializer.endTag( "properties" );
            }
            serializer.endTag( tagName );
        }
        serializer.endDocument();
    }

}
