package nut.interpolation;

import nut.artifact.Artifact;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Goal;
import nut.model.Model;
import nut.model.Repository;
import nut.model.XmlWriter;

import nut.logging.Log;

/**
 * Interpolator replaces every data as "${xx} by the value of the xx property.
 * For example: <version>${project.version}</version> is replaced by the project version in the project model
 */
public class Interpolator
{
    
    private Model model;

    private Artifact artifact;

    // Building time
    private long time;
    // 
    boolean buildDone;
    boolean buildSuccess;

    private Log log;

    // in case of failure
    private Exception cause;
    private String task;

    public Interpolator()
    {
    }

    public Model interpolatedModel( Model model )
    {
      return model;
    }

}

