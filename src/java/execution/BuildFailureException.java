package nut.execution;

/**
 * One or more builds failed.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id: BuildFailureException.java 495147 2007-01-11 07:47:53Z jvanzyl $
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
