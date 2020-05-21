package nut.model;

import java.io.StringWriter;
import java.util.Iterator;

import nut.model.Dependency;
import nut.model.Layout;
import nut.model.Model;

import nut.json.JsonSerializer;

public class JsonWriter {

    private JsonSerializer serializer;

    /**
     * Method writeLayout.
     *
     * @param layout
     * @throws java.io.IOException
     */
    private void writeLayout( Layout layout )
        throws java.io.IOException
    {
            serializer.startObject( "layout" );
            serializer.element( "sourceDirectory", layout.getSourceDirectory() );
            serializer.element( "resourceDirectory", layout.getResourceDirectory() );
            serializer.element( "testSourceDirectory", layout.getTestSourceDirectory() );
            serializer.element( "testResourceDirectory", layout.getTestResourceDirectory() );
            serializer.element( "targetDirectory", layout.getTargetDirectory() );
            serializer.element( "outputDirectory", layout.getOutputDirectory() );
            serializer.element( "testOutputDirectory", layout.getTestOutputDirectory() );
            serializer.element( "testReportDirectory", layout.getTestReportDirectory() );
            serializer.element( "testSuite", layout.getTestSuite() );
            serializer.endObject();
    }

    /**
     * Method writeDependency.
     *
     * @param dependency
     * @throws java.io.IOException
     */
    private void writeDependency( Dependency dependency )
        throws java.io.IOException
    {
            serializer.startObject();
            serializer.element( "groupId", dependency.getGroupId() );
            serializer.element( "artifactId", dependency.getArtifactId() );
            serializer.element( "version", dependency.getVersion() );
            serializer.element( "type", dependency.getType() );
            serializer.element( "scope", dependency.getScope() );
            serializer.endObject();
    }

    /**
     * Method writeRepository.
     *
     * @param repository
     * @throws java.io.IOException
     */
    private void writeRepository( Repository repository )
        throws java.io.IOException
    {
            serializer.startObject();
            serializer.element( "name", repository.getName() );
            serializer.element( "layout", repository.getLayout() );
            serializer.element( "url", repository.getURL() );
            serializer.endObject();
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
        serializer = new JsonSerializer(sWriter);
        serializer.startDocument();
        serializer.startObject( "project" );
        serializer.element( "parent", model.getParent() );
        serializer.element( "groupId", model.getGroupId() );
        serializer.element( "artifactId", model.getArtifactId() );
        serializer.element( "version", model.getVersion() );
        serializer.element( "packaging", model.getPackaging() );
        serializer.element( "description", model.getDescription() );
        writeLayout( (Layout) model.getLayout() );
        serializer.element( "build", model.getBuild() );
        if ( model.getModules().size() > 0 ) {
            serializer.startObject( "modules" );
            serializer.startList( "module" );
            for ( Iterator iter = model.getModules().iterator(); iter.hasNext(); ) {
                String module = (String) iter.next();
                serializer.element( module );
            }
            serializer.endList();
            serializer.endObject();
        }
        
        if ( model.getDependencies().size() > 0 ) {
            serializer.startObject( "dependencies" );
            serializer.startList( "dependency" );
            for ( Iterator iter = model.getDependencies().iterator(); iter.hasNext(); ) {
                Dependency o = (Dependency) iter.next();
                writeDependency( o );
            }
            serializer.endList();
            serializer.endObject();
        }
        if ( model.getRepositories().size() > 0 ) {
            serializer.startObject( "repositories" );
            serializer.startList( "repository" );
            for ( Iterator iter = model.getRepositories().iterator(); iter.hasNext(); ) {
                Repository o = (Repository) iter.next();
                writeRepository( o );
            }
            serializer.endList();
            serializer.endObject();
        }
        if ( model.getProperties().size() > 0 ) {
            serializer.startObject( "properties" );
            for ( Iterator iter = model.getProperties().keySet().iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
                String value = (String) model.getProperties().get( key );
                serializer.element( key, value );
            }
            serializer.endObject();
        }
        serializer.endObject();
        serializer.endDocument();
    }
}
