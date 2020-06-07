package nut.build.archive;

public class ArchiverException
    extends Exception
{
    public ArchiverException( String message )
    {
        super( message );
    }

    public ArchiverException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
