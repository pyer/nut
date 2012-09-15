package nut.execution;

import java.net.URL;

/**
 * Implementation of <code>ClassRealm</code>.  The realm is the class loading gateway.
 */
public class NutClassRealm
{
    private ClassLoader    foreignClassLoader;
    private NutClassLoader nutClassLoader;

    public NutClassRealm( ClassLoader foreignClassLoader, URL constituent )
    {
        //System.out.println( "ClassRealm: " + constituent );
        this.foreignClassLoader = foreignClassLoader;
        this.nutClassLoader     = new NutClassLoader( this, constituent );
    }
    // ----------------------------------------------------------------------
    // ClassLoader API
    // ----------------------------------------------------------------------

    public Class loading( String name ) throws ClassNotFoundException
    {
        if ( foreignClassLoader != null )
        {
            try
            {
                return foreignClassLoader.loadClass( name );
            }
            catch ( ClassNotFoundException e )
            {
                //return nutClassLoader.loadClassDirect( name );
            }
        }
        return nutClassLoader.loadClassDirect( name );
    }

}
