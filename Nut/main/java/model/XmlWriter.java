package nut.model;

import java.io.StringWriter;
import java.util.Iterator;

import nut.model.Dependency;
import nut.model.Layout;
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
     * Method writeLayout.
     *
     * @param layout
     * @param tagName
     * @param serializer
     * @throws java.io.IOException
     */
    private void writeLayout( Layout layout, String tagName, XmlSerializer serializer )
        throws java.io.IOException
    {
            serializer.startTag( tagName );
            writeElement( "sourceDirectory", layout.getSourceDirectory(), serializer );
            writeElement( "resourceDirectory", layout.getResourceDirectory(), serializer );
            writeElement( "testSourceDirectory", layout.getTestSourceDirectory(), serializer );
            writeElement( "testResourceDirectory", layout.getTestResourceDirectory(), serializer );
            writeElement( "targetDirectory", layout.getTargetDirectory(), serializer );
            writeElement( "outputDirectory", layout.getOutputDirectory(), serializer );
            writeElement( "testOutputDirectory", layout.getTestOutputDirectory(), serializer );
            writeElement( "testReportDirectory", layout.getTestReportDirectory(), serializer );
            writeElement( "testSuite", layout.getTestSuite(), serializer );
            serializer.endTag( tagName );
    } //-- void writeLayout( Layout, String, XmlSerializer )

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
            serializer.startTag( tagName );
            writeElement( "groupId", dependency.getGroupId(), serializer );
            writeElement( "artifactId", dependency.getArtifactId(), serializer );
            writeElement( "version", dependency.getVersion(), serializer );
            writeElement( "type", dependency.getType(), serializer );
            writeElement( "scope", dependency.getScope(), serializer );
            serializer.endTag( tagName );
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
            serializer.startTag( tagName );
            writeElement( "name", repository.getName(), serializer );
            writeElement( "layout", repository.getLayout(), serializer );
            writeElement( "url", repository.getURL(), serializer );
            serializer.endTag( tagName );
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
        serializer.startDocument();
        serializer.startTag( tagName );
            writeElement( "parent", model.getParent(), serializer );
            writeElement( "groupId", model.getGroupId(), serializer );
            writeElement( "artifactId", model.getArtifactId(), serializer );
            writeElement( "version", model.getVersion(), serializer );
            writeElement( "packaging", model.getPackaging(), serializer );
            writeElement( "description", model.getDescription(), serializer );
            writeLayout( (Layout) model.getLayout(), "layout", serializer );
            writeElement( "build", model.getBuild(), serializer );
            if ( model.getModules().size() > 0 ) {
                serializer.startTag( "modules" );
                for ( Iterator iter = model.getModules().iterator(); iter.hasNext(); ) {
                    String module = (String) iter.next();
                    writeElement( "module", module, serializer );
                }
                serializer.endTag( "modules" );
            }
            if ( model.getDependencies().size() > 0 ) {
                serializer.startTag( "dependencies" );
                for ( Iterator iter = model.getDependencies().iterator(); iter.hasNext(); ) {
                    Dependency o = (Dependency) iter.next();
                    writeDependency( o, "dependency", serializer );
                }
                serializer.endTag( "dependencies" );
            }
            if ( model.getRepositories().size() > 0 ) {
                serializer.startTag( "repositories" );
                for ( Iterator iter = model.getRepositories().iterator(); iter.hasNext(); ) {
                    Repository o = (Repository) iter.next();
                    writeRepository( o, "repository", serializer );
                }
                serializer.endTag( "repositories" );
            }
            if ( model.getProperties().size() > 0 ) {
                serializer.startTag( "properties" );
                for ( Iterator iter = model.getProperties().keySet().iterator(); iter.hasNext(); ) {
                    String key = (String) iter.next();
                    String value = (String) model.getProperties().get( key );
                    writeElement( "" + key + "", value, serializer );
                }
                serializer.endTag( "properties" );
            }
        serializer.endTag( tagName );
        serializer.endDocument();
    }

}
