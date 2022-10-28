package nut.model;

import java.io.File;

public class Dependency implements java.io.Serializable {

    // Variables
    // ----------------------------------------------------------------------
    /**
     * The path of the dependency in the repository
     */
    private String path;
    
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

    public String getGroup()
    {
        String group = "";
        int i = path.lastIndexOf( '/' );
        if ( i>1 ) {
          group = path.substring( 0, i );
        }
        return group;
    }

    // -------------------------------------------------------------
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Dependency d )
    {
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
    public boolean isNotHere( String repositoryRoot )
    {
        File f = new File( repositoryRoot + getPath() );
        return (!f.exists() || f.isDirectory() );
    }

    // ----------------------------------------------------------------------
}
