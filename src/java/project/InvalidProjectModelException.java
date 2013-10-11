package nut.project;

public class InvalidProjectModelException
    extends ProjectBuildingException
{
    private final String pomLocation;

    public InvalidProjectModelException( String projectId, String pomLocation, String message, Throwable cause )
    {
        super( projectId, message, cause );
        this.pomLocation = pomLocation;
    }

    public InvalidProjectModelException( String projectId, String pomLocation, String message )
    {
        super( projectId, message );

        this.pomLocation = pomLocation;
    }

    public final String getPomLocation()
    {
        return pomLocation;
    }

    public String getMessage() 
    {
        return super.getMessage() + " at " + this.pomLocation;
    }

}
