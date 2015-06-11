package nut.artifact;

import nut.artifact.InvalidArtifactRTException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Description of an artifact.
 */
public class Artifact
{
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String type;
    
    private final String repository;

    public Artifact( String groupId, String artifactId, String version, String type )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;

        if ( (groupId == null) || groupId.isEmpty() ) {
            throw new InvalidArtifactRTException( "The groupId of the artifact " + this.toString() + " cannot be empty." );
        }

        if ( artifactId == null || artifactId.isEmpty() ) {
            throw new InvalidArtifactRTException( "The artifactId of the artifact " + this.toString() + " cannot be empty." );
        }

        if ( version == null || version.isEmpty() ) {
            throw new InvalidArtifactRTException( "The version of the artifact " + this.toString() + " cannot be empty." );
        }

        if ( type == null || type.isEmpty() ) {
            throw new InvalidArtifactRTException( "The type of the artifact " + this.toString() + " cannot be empty." );
        }
        
        this.repository = System.getProperty( "nut.home" );
        if ( this.repository == null ) {
            throw new InvalidArtifactRTException( "The 'nut.home' system property is undefined." );
        }
    }

    // ----------------------------------------------------------------------
    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public String getType()
    {
        return type;
    }
    
    public String getPath()
    {
        String group = groupId.replace( '.', File.separatorChar );
        return group + File.separator + artifactId + "-" + version + "." + type;
    }

    public File getFile()
    {
        return new File ( this.repository + File.separator + getPath() );
    }

    // ----------------------------------------------------------------------
    // relative file path in a maven repository
    // used to build a URL ('/' instead of separatorChar)
    // Example: "http://search.maven.org/remotecontent?filepath=org/testng/testng/6.8.7/testng-6.8.7.jar"
    public String mavenFilePath()
    {
        String group = getGroupId().replace( '.', '/' );
        return '/' + group + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + "." + type;
    }
    // ----------------------------------------------------------------------
    // relative file path in a nut repository
    // used to build a URL ('/' instead of separatorChar)
    public String nutFilePath()
    {
        String group = getGroupId().replace( '.', '/' );
        return '/' + group + "/" + artifactId + "-" + version + "." + type;
    }
    // ----------------------------------------------------------------------
    // check if the artifact file is present in the local repository and readable
    public boolean isPresent()
        throws SecurityException
    {
      return getFile().isFile();
    }

    public FileInputStream fileInputStream()
        throws FileNotFoundException
    {
        File f = getFile();
        return new FileInputStream( f );
    }

    public FileOutputStream fileOutputStream()
        throws IOException
    {
        File f = getFile();
        //does destination directory exist ?
        if( !f.getParentFile().exists() ) {
            f.getParentFile().mkdirs();
        }
        return new FileOutputStream( f );
    }

    // ----------------------------------------------------------------------
    // Object overrides
    // ----------------------------------------------------------------------
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( getGroupId() );
        sb.append( ":" );
        sb.append( getArtifactId() );
        sb.append( ":" );
        sb.append( getType() );
        sb.append( ":" );
        sb.append( getVersion() );
        return sb.toString();
    }

}
