package nut.execution;

/**
 * One or more builds failed.
 *
 */
public class BuildFailureException
    extends Exception
{
    public BuildFailureException( String message )
    {
        super( message );
    }

    public BuildFailureException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
