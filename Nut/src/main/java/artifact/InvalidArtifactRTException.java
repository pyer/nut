package nut.artifact;

/**
 * Exception thrown when the identity of an artifact can not be established,
 * eg. one of groupId, artifactId, version or type is null.   
 */
public class InvalidArtifactRTException
    extends RuntimeException
{
    private final String message;

    public InvalidArtifactRTException( String message )
    {
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }
}
