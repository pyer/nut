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
    private String groupId;
    private String artifactId;
    private String version;
    private final String type;
    private final String classifier;
    
    private String repository;

    public Artifact( String groupId, String artifactId, String version, String type, String classifier )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
        this.classifier = classifier;

        if ( (groupId == null) || groupId.isEmpty() ) {
            throw new InvalidArtifactRTException( groupId, artifactId, version, type,
                                                  "The groupId cannot be empty." );
        }

        if ( artifactId == null ) {
            throw new InvalidArtifactRTException( groupId, artifactId, version, type,
                                                  "The artifactId cannot be empty." );
        }

        if ( version == null ) {
            throw new InvalidArtifactRTException( groupId, artifactId, version, type,
                                                  "The version cannot be empty." );
        }

        if ( type == null ) {
            throw new InvalidArtifactRTException( groupId, artifactId, version, type,
                                                  "The type cannot be empty." );
        }
        
        this.repository = System.getProperty( "nut.home" );
        if ( this.repository == null ) {
            throw new InvalidArtifactRTException( groupId, artifactId, version, type,
                                                  "The 'nut.home' property is undefined." );
        }
    }

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

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getType()
    {
        return type;
    }
    
    public String getClassifier()
    {
        return classifier;
    }

    public boolean hasClassifier()
    {
        return ( (classifier!=null) && !classifier.isEmpty() );
    }

    public String getPath()
    {
        String group = getGroupId().replace( '.', File.separatorChar );
        String path  = group+File.separator+artifactId+"-"+getVersion();
        if ( hasClassifier() ) {
            path = path + "-" + classifier;
        }
        path = path + "." + type;
        return path;
    }

    public String getFile()
    {
        return new File ( repository + File.separator + getPath() );
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
        if ( hasClassifier() ) {
            sb.append( ":" );
            sb.append( getClassifier() );
        }
        sb.append( ":" );
        sb.append( getVersion() );
        return sb.toString();
    }

}
