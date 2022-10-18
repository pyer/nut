package nut.build;

/**
 * Exception that occurs when the project list has cyclic dependencies instead of ignoring one.
 *
 */
public class CyclicProjectException
    extends Exception
{
    public CyclicProjectException( String message )
    {
        super( message );
    }

    public CyclicProjectException( String message, Exception e )
    {
        super( message, e );
    }
}
