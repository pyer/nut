package nut.workers;

/**
 * Exception that occurs when the project list contains duplicate projects instead of ignoring one.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id: DuplicateProjectException.java 640549 2008-03-24 20:05:11Z bentmann $
 */
public class DuplicateProjectException
    extends Exception
{
    public DuplicateProjectException( String message )
    {
        super( message );
    }

    public DuplicateProjectException( String message, Exception e )
    {
        super( message, e );
    }
}
