package nut.build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vertex implements Cloneable, Serializable
{
    // ------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------
    private String label = null;

    List<Vertex> children = new ArrayList<>();

//    List<Vertex> parents = new ArrayList<>();

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public Vertex( final String label )
    {
        this.label = label;
    }

    // ------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------

    public String getLabel()
    {
        return label;
    }

    public void addEdgeTo( final Vertex vertex )
    {
        children.add( vertex );
    }

    public void removeEdgeTo( final Vertex vertex )
    {
        children.remove( vertex );
    }
/*
    public void addEdgeFrom( final Vertex vertex )
    {
        parents.add( vertex );
    }

    public void removeEdgeFrom( final Vertex vertex )
    {
        parents.remove( vertex );
    }
*/
    public List<Vertex> getChildren()
    {
        return children;
    }

}
