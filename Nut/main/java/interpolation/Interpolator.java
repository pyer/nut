package nut.interpolation;

import nut.model.Model;

/**
 * Interpolator replaces every data as "${xx} by the value of the xx property.
 * For example: <version>${project.version}</version> is replaced by the project version in the project model
 *
 * Does nothing at the moment.
 * will be done later, when I'll need this feature
 */
public class Interpolator
{
//    private Model model;
//    private Artifact artifact;
//    private Log log;

//    public Interpolator()
//    {
//    }

    public Model interpolatedModel( Model model )
    {
      // This method is intentionally empty
      return model;
    }
}
