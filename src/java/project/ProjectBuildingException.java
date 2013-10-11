package nut.project;

public class ProjectBuildingException
    extends Exception
{
    private final String projectId;

    public ProjectBuildingException( String projectId, String message )
    {
        super( message );
        this.projectId = projectId;
    }

    public ProjectBuildingException( String projectId, String message, Throwable cause )
    {
        super( message, cause );
        this.projectId = projectId;
    }
    
    public String getProjectId()
    {
        return projectId;
    }

    public String getMessage() 
    {
        return super.getMessage() + " for project " + this.projectId;
    }
}
