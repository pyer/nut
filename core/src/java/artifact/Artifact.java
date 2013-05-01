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

    private boolean resolved;
    
    public Artifact( String groupId, String artifactId, String version, String type, String classifier )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
        this.classifier = classifier;
        this.resolved = false;

        if ( (groupId == null) || groupId.isEmpty() )
        {
            throw new InvalidArtifactRTException( groupId, artifactId, version, type,
                                                  "The groupId cannot be empty." );
        }

        if ( artifactId == null )
        {
            throw new InvalidArtifactRTException( groupId, artifactId, version, type,
                                                  "The artifactId cannot be empty." );
        }

        if ( version == null )
        {
            throw new InvalidArtifactRTException( groupId, artifactId, version, type,
                                                  "The version cannot be empty." );
        }

        if ( type == null )
        {
            throw new InvalidArtifactRTException( groupId, artifactId, version, type,
                                                  "The type cannot be empty." );
        }
        
        this.repository = System.getProperty( "nut.home" );
        if ( this.repository == null )
        {
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

    public void setResolved( boolean resolved )
    {
        this.resolved = resolved;
    }

    public boolean isResolved()
    {
        return resolved;
    }

    public String getPath()
    {
        String group = getGroupId().replace( '.', File.separatorChar );
        String path  = group+File.separator+artifactId+"-"+getVersion();
        if ( hasClassifier() )
        {
            path = path + "-" + classifier;
        }
        path = path + "." + type;
        return path;
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
        if ( hasClassifier() )
        {
            sb.append( ":" );
            sb.append( getClassifier() );
        }
        sb.append( ":" );
        sb.append( getVersion() );
        return sb.toString();
    }

    public int hashCode()
    {
        int result = 17;
        result = 37 * result + groupId.hashCode();
        result = 37 * result + artifactId.hashCode();
        result = 37 * result + type.hashCode();
        if ( version != null )
        {
            result = 37 * result + version.hashCode();
        }
        result = 37 * result + ( classifier != null ? classifier.hashCode() : 0 );
        return result;
    }

    public boolean equals( Object o )
    {
        if ( o == this )
        {
            return true;
        }

        if ( !( o instanceof Artifact ) )
        {
            return false;
        }

        Artifact a = (Artifact) o;

        if ( !a.getGroupId().equals( groupId ) )
        {
            return false;
        }
        else if ( !a.getArtifactId().equals( artifactId ) )
        {
            return false;
        }
        else if ( !a.getVersion().equals( version ) )
        {
            return false;
        }
        else if ( !a.getType().equals( type ) )
        {
            return false;
        }
        else if ( a.getClassifier() == null ? classifier != null : !a.getClassifier().equals( classifier ) )
        {
            return false;
        }
        return true;
    }

    public int compareTo( Object o )
    {
        Artifact a = (Artifact) o;

        int result = groupId.compareTo( a.getGroupId() );
        if ( result == 0 )
        {
            result = artifactId.compareTo( a.getArtifactId() );
            if ( result == 0 )
            {
                result = type.compareTo( a.getType() );
                if ( result == 0 )
                {
                    if ( classifier == null )
                    {
                        if ( a.getClassifier() != null )
                        {
                            result = 1;
                        }
                    }
                    else
                    {
                        if ( a.getClassifier() != null )
                        {
                            result = classifier.compareTo( a.getClassifier() );
                        }
                        else
                        {
                            result = -1;
                        }
                    }
                    if ( result == 0 )
                    {
                        // We don't consider the version range in the comparison, just the resolved version
                        result = version.compareTo( a.getVersion() );
                    }
                }
            }
        }
        return result;
    }

}
