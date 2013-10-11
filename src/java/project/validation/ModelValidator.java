package nut.project.validation;

import nut.artifact.Artifact;
import nut.model.Build;
import nut.model.Dependency;
import nut.model.Model;
import nut.model.Plugin;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class ModelValidator
{
    private static final String ID_REGEX = "[A-Za-z0-9_\\-.]+";

    ///////////////////////////////////////////////////////////////////////////
    // ModelValidator Implementation
    private Model model;

    public ModelValidator( Model model )
    {
        this.model = model;
    }

    public void validate()
        throws ModelValidationException
    {
        validateId( "groupId", model.getGroupId() );
        validateId( "artifactId", model.getArtifactId() );
        validateStringNotEmpty( "version", model.getVersion() );
        validateStringNotEmpty( "packaging", model.getPackaging() );
        if ( !model.getModules().isEmpty() && !"modules".equals( model.getPackaging() ) )
        {
            throw new ModelValidationException( "Packaging '" + model.getPackaging() +
                                                "' is invalid. Aggregator projects require 'modules' as packaging." );
        }
        

        for ( Iterator it = model.getDependencies().iterator(); it.hasNext(); )
        {
            Dependency d = (Dependency) it.next();

            validateId( "dependencies.dependency.artifactId", d.getArtifactId() );

            validateId( "dependencies.dependency.groupId", d.getGroupId() );

            validateDepStringNotEmpty( d, "dependencies.dependency.type", d.getType() );

            validateDepStringNotEmpty( d, "dependencies.dependency.version", d.getVersion() );
        }

        Build build = model.getBuild();
        if ( build != null )
        {
            List plugins = build.getPlugins();
            if ( plugins != null )
            {
                for ( Iterator it = plugins.iterator(); it.hasNext(); )
                {
                    Plugin plugin = (Plugin) it.next();
                    validateStringNotEmpty( "build.plugins.plugin.groupId", plugin.getGroupId() );
                    validateStringNotEmpty( "build.plugins.plugin.artifactId", plugin.getArtifactId() );
                    validateStringNotEmpty( "build.plugins.plugin.version", plugin.getVersion() );
                    validateStringNotEmpty( "build.plugins.plugin.goal", plugin.getGoal() );
                }
            }
        }
    }

    // ----------------------------------------------------------------------
    // Field validation
    // ----------------------------------------------------------------------

    private void validateId( String fieldName, String id )
        throws ModelValidationException
    {
        if ( !id.matches( ID_REGEX ) )
            throw new ModelValidationException( "'" + fieldName + "' with value '" + id + "' does not match a valid id pattern." );
    }

    private void validateStringNotEmpty( String fieldName, String string )
        throws ModelValidationException
    {
        if ( string == null )
            throw new ModelValidationException( "'" + fieldName + "' is null." );
        if ( string.length() <1 )
            throw new ModelValidationException( "'" + fieldName + "' is empty." );
    }

    private void validateDepStringNotEmpty( Dependency d, String fieldName, String string )
        throws ModelValidationException
    {
        if ( string == null )
            throw new ModelValidationException( "'" + fieldName + "' is null for " + d.getGroupId() + ":" + d.getArtifactId() );
        if ( string.length() <1 )
            throw new ModelValidationException( "'" + fieldName + "' is empty for " + d.getGroupId() + ":" + d.getArtifactId() );
    }

}
