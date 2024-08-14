package nut.model;

import nut.model.ValidationException;
import java.io.File;

public class Dependency implements java.io.Serializable {

    // Variables
    // ----------------------------------------------------------------------
    /**
     * The path of the dependency in the repository
     */
    private String path;
    private String realPath;
    
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

    public String getSnapshotPath()
    {
        String snapshotPath = path;
        int i = path.lastIndexOf( '.' );
        if ( i>1 ) {
          snapshotPath = path.substring( 0, i ) + "-SNAPSHOT." + getPackaging();
        } else {
          snapshotPath = path + "-SNAPSHOT";
        }
        return snapshotPath;
    }

    public void setRealPath( String path )
    {
        this.realPath = path;
    }

    public String getRealPath()
    {
        if ( this.realPath == null ) {
          this.realPath = this.path;
        }
        return this.realPath;
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

    public String getPackaging()
    {
        String packaging = "";
        int i = path.lastIndexOf( '.' );
        int l = path.length();
        if ( i>1 && i<l ) {
          packaging = path.substring( i+1, l );
        }
        return packaging;
    }

    // Returns "/lib" when path is "/a/b/c/lib"
    // Return "/" when path is "/a/b/" or "abc"
    public String getLibName()
    {
        String name = "/";
        int i = path.lastIndexOf( '/' );
        if ( i>=0 ) {
          name = path.substring( i );
        }
        return name;
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
     * Dependency validation
     */
    public void validate() throws ValidationException
    {
        if ( path == null ) {
            throw new ValidationException( "dependency is null" );
        }
        if ( path.isEmpty() ) {
            throw new ValidationException( "dependency is empty" );
        }
        if ( path.contains(" \t") ) {
            throw new ValidationException( "dependency contains space" );
        }
        if ( ! path.startsWith("/") ) {
            throw new ValidationException( "dependency '" + path + "' must start with '/'" );
        }
        if ( path.lastIndexOf("/") == 0 ) {
            throw new ValidationException( "dependency '" + path + "' is not a file name" );
        }
        if ( path.lastIndexOf("/") == (path.length()-1) ) {
            throw new ValidationException( "dependency '" + path + "' is not a file name" );
        }
    }

    /**
     * Check if the dependency is present in the local repository
     */
    public boolean releaseIsHere( String repositoryRoot )
    {
        File f = new File( repositoryRoot + getPath() );
        return (f.exists() && !f.isDirectory() );
    }

    public boolean snapshotIsHere( String repositoryRoot )
    {
        File f = new File( repositoryRoot + getSnapshotPath() );
        return (f.exists() && !f.isDirectory() );
    }

    // ----------------------------------------------------------------------
}
