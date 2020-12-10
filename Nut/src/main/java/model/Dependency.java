package nut.model;

import java.io.File;

public class Dependency implements java.io.Serializable {

    // Variables
    // ----------------------------------------------------------------------
    /**
     * The path of the dependency in the repository
     */
    private String path;
    
    /**
     * The parts of the dependency Id
     */
    private String group   = "";
    private String name    = "";
    private String version = "";
    private String suffix  = "";

    // Constructor
    // ----------------------------------------------------------------------
    public Dependency(String path)
    {
        this.path = path;
    }

    // -------------------------------------------------------------
    public String getPath()
    {
        return this.path;
    }

    /* getId is not used
    */
    public String getId()
    {
//        String str = this.path.startsWith( "/" ) ? this.path.substring( 1 ) : this.path;
        String str = getPath();
        int g = str.lastIndexOf( '/' );
        if ( g > 1 ) {
          group = str.substring( 1, g ).replace( '/', '.' );
        }
        int s = str.lastIndexOf( '.' );
        if ( s > 0 ) {
          suffix = str.substring( s + 1 );
        } else {
          s = str.length();
        }
        if ( g + 1 < s ) {
          String middle = str.substring( g + 1, s );
          int v = middle.lastIndexOf( '-' );
          if ( middle.endsWith( "-SNAPSHOT" ) ) {
            v = middle.substring( 0, middle.length() - 10 ).lastIndexOf( '-' );
          }
          if ( v > 0 ) {
            version = middle.substring( v + 1 );
            name = middle.substring( 0, v );
          } else {
            name = middle;
          }
        }
        return group + ":" + name + ":" + version + ":" + suffix;
    }

    // -------------------------------------------------------------

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof Dependency ) )
        {
            return false;
        }

        Dependency d  = (Dependency) o;
        return getPath().equals( d.getPath() );
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getPath().hashCode();
    }

    // -------------------------------------------------------------

    /**
     * Check if the dependency is present in the local repository
     */
    public boolean isPresent( String repositoryRoot )
    {
        File f = new File( repositoryRoot + File.separator + getPath() );
        return (f.exists() && !f.isDirectory() );
    }

    // ----------------------------------------------------------------------
}
