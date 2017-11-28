package nut.model;

import java.io.StringWriter;
import java.util.Iterator;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Goal;
import nut.model.Model;

import nut.json.JsonSerializer;

public class JsonWriter {

    private JsonSerializer serializer;

    /**
     * Method writeGoal.
     *
     * @param goal
     * @throws java.io.IOException
     */
    private void writeGoal( Goal goal )
        throws java.io.IOException
    {
            serializer.startObject(null);
            serializer.element( "name", goal.getName() );
            if ( goal.hasConfiguration() ) {
                serializer.startObject( "configuration" );
                for ( Iterator iter = goal.getConfiguration().keySet().iterator(); iter.hasNext(); ) {
                    String key = (String) iter.next();
                    String value = (String) goal.getConfiguration().getProperty( key );
                    serializer.element( key, value );
                }
                serializer.endObject();
            }
            serializer.endObject();
    }

    /**
     * Method writeBuild.
     *
     * @param build
     * @throws java.io.IOException
     */
    private void writeBuild( Build build )
        throws java.io.IOException
    {
            serializer.startObject( "build" );
            serializer.element( "sourceDirectory", build.getSourceDirectory() );
            serializer.element( "resourceDirectory", build.getResourceDirectory() );
            serializer.element( "testSourceDirectory", build.getTestSourceDirectory() );
            serializer.element( "testResourceDirectory", build.getTestResourceDirectory() );
            serializer.element( "targetDirectory", build.getTargetDirectory() );
            serializer.element( "outputDirectory", build.getOutputDirectory() );
            serializer.element( "testOutputDirectory", build.getTestOutputDirectory() );
            serializer.element( "testReportDirectory", build.getTestReportDirectory() );
            if ( build.getGoals() != null && build.getGoals().size() > 0 ) {
                serializer.startObject( "goals" );
                serializer.startList( "goal" );
                for ( Iterator iter = build.getGoals().iterator(); iter.hasNext(); ) {
                    Goal o = (Goal) iter.next();
                    writeGoal( o );
                }
                serializer.endList();
                serializer.endObject();
            }
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
        writeBuild( (Build) model.getBuild() );
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
