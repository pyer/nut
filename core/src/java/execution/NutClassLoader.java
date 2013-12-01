package nut.execution;

import java.net.URL;
import java.net.URLClassLoader;

/** Classloader for <code>ClassRealm</code>s.
 */
public class NutClassLoader extends URLClassLoader
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The realm. */
    protected NutClassRealm realm;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param realm The realm for which this loads.
     */
    public NutClassLoader( NutClassRealm realm, URL constituent )
    {
        super( new URL[0], null );
        this.realm = realm;
        addURL( constituent );
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------
    /** Load a class directly from this classloader without
     *  defering through any other <code>ClassRealm</code>.
     *
     *  @param name The name of the class to load.
     *
     *  @return The loaded class.
     *
     *  @throws ClassNotFoundException If the class could not be found.
     */
    public Class loadClassDirect(String name) throws ClassNotFoundException
    {
        return super.loadClass( name, false );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //     java.lang.ClassLoader
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /** Load a class.
     *
     *  @param name The name of the class to load.
     *  @param resolve If <code>true</code> then resolve the class.
     *
     *  @return The loaded class.
     *
     *  @throws ClassNotFoundException If the class cannot be found.
     */
    protected Class loadClass(String name, boolean resolve ) throws ClassNotFoundException
    {
        //System.out.println( "NutClassLoader:loadClass " + name );
        return this.realm.loading( name );
    }

}
