package nut.project;

/**
 * Exception thrown when a dependency artifact is not found in any repositories.
 */
public class DependencyNotFoundException
    extends Exception
{
    private final String message;

    public DependencyNotFoundException( String message )
    {
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }
}
