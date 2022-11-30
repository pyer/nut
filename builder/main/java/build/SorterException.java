package nut.build;

public class SorterException extends Exception
{
    public SorterException( final String message )
    {
        super( message );
    }

    public SorterException( final String message, Exception e )
    {
        super( message, e );
    }
}
