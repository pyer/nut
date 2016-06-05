package nut.workers;

/**
 * One or more builds failed.
 *
 */
public class AssemblerException
    extends Exception
{
    public AssemblerException( String message )
    {
        super( message );
    }

    public AssemblerException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
