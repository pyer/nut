package nut.model;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import nut.model.Dependency;
import nut.model.Layout;
import nut.model.Project;

import nut.json.JsonSerializer;

public class JsonWriter {

    private JsonSerializer serializer;

    /**
     * Method writeLayout.
     *
     * @param layout
     * @throws IOException
     */
    private void writeLayout( Layout layout )
        throws IOException
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
     * @throws IOException
     */
    private void writeDependency( Dependency dependency )
        throws IOException
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
     * @throws IOException
     */
    private void writeRepository( Repository repository )
        throws IOException
    {
            serializer.startObject();
            serializer.element( "name", repository.getName() );
            serializer.element( "layout", repository.getLayout() );
            serializer.element( "url", repository.getURL() );
            serializer.endObject();
    }

    /**
     * Method writeProject.
     *
     * @param sWriter
     * @param project
     * @throws IOException
     */
    public void writeProject( StringWriter sWriter, Project project )
        throws IOException
    {
        serializer = new JsonSerializer(sWriter);
        serializer.startDocument();
        serializer.startObject( "project" );
        serializer.element( "parent", project.getParent() );
        serializer.element( "groupId", project.getGroupId() );
        serializer.element( "artifactId", project.getArtifactId() );
        serializer.element( "version", project.getVersion() );
        serializer.element( "packaging", project.getPackaging() );
        serializer.element( "description", project.getDescription() );
        writeLayout( (Layout) project.getLayout() );
        serializer.element( "build", project.getBuild() );
        if ( project.getModules().size() > 0 ) {
            serializer.startList( "modules" );
            for ( Iterator iter = project.getModules().iterator(); iter.hasNext(); ) {
                String module = (String) iter.next();
                serializer.element( module );
            }
            serializer.endList();
        }
        
        if ( project.getDependencies().size() > 0 ) {
            serializer.startList( "dependencies" );
            for ( Iterator iter = project.getDependencies().iterator(); iter.hasNext(); ) {
                Dependency o = (Dependency) iter.next();
                writeDependency( o );
            }
            serializer.endList();
        }
        if ( project.getRepositories().size() > 0 ) {
            serializer.startList( "repositories" );
            for ( Iterator iter = project.getRepositories().iterator(); iter.hasNext(); ) {
                Repository o = (Repository) iter.next();
                writeRepository( o );
            }
            serializer.endList();
        }
        if ( project.getProperties().size() > 0 ) {
            serializer.startObject( "properties" );
            for ( Iterator iter = project.getProperties().keySet().iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
                String value = (String) project.getProperties().get( key );
                serializer.element( key, value );
            }
            serializer.endObject();
        }
        serializer.endObject();
        serializer.endDocument();
    }

}
