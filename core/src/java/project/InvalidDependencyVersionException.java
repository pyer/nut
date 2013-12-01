package nut.project;

/**
 * Thrown if a dependency has an invalid version.
 *
 */
public class InvalidDependencyVersionException
    extends Exception
{
    public InvalidDependencyVersionException( String message, Exception cause )
    {
        super( message, cause );
    }
}
