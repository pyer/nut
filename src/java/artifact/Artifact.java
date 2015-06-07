package nut.artifact;

import nut.artifact.InvalidArtifactRTException;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Description of an artifact.
 */
public class Artifact
{
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String type;
    
    private final String repository;

    public Artifact( String groupId, String artifactId, String version, String type )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;

        if ( (groupId == null) || groupId.isEmpty() ) {
            throw new InvalidArtifactRTException( "The groupId of the artifact " + this.toString() + " cannot be empty." );
        }

        if ( artifactId == null || artifactId.isEmpty() ) {
            throw new InvalidArtifactRTException( "The artifactId of the artifact " + this.toString() + " cannot be empty." );
        }

        if ( version == null || version.isEmpty() ) {
            throw new InvalidArtifactRTException( "The version of the artifact " + this.toString() + " cannot be empty." );
        }

        if ( type == null || type.isEmpty() ) {
            throw new InvalidArtifactRTException( "The type of the artifact " + this.toString() + " cannot be empty." );
        }
        
        this.repository = System.getProperty( "nut.home" );
        if ( this.repository == null ) {
            throw new InvalidArtifactRTException( "The 'nut.home' system property is undefined." );
        }
    }

    // ----------------------------------------------------------------------
    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public String getType()
    {
        return type;
    }
    
    public String getPath()
    {
        String group = getGroupId().replace( '.', File.separatorChar );
        return group + File.separator + artifactId + "-" + getVersion() + "." + type;
    }

    public File getFile()
    {
        return new File ( this.repository + File.separator + getPath() );
    }

    // ----------------------------------------------------------------------
    // Object overrides
    // ----------------------------------------------------------------------
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( getGroupId() );
        sb.append( ":" );
        sb.append( getArtifactId() );
        sb.append( ":" );
        sb.append( getType() );
        sb.append( ":" );
        sb.append( getVersion() );
        return sb.toString();
    }

}
