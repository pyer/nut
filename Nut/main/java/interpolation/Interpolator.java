package nut.interpolation;

import nut.model.Project;

/**
 * Interpolator replaces every data as "${xx} by the value of the xx property.
 * For example: <version>${project.version}</version> is replaced by the project version in the project project
 *
 * Does nothing at the moment.
 * will be done later, when I'll need this feature
 */
public class Interpolator
{
//    private Project project;
//    private Artifact artifact;
//    private Log log;

//    public Interpolator()
//    {
//    }

    public Project interpolatedProject( Project project )
    {
      // This method is intentionally empty
      return project;
    }
}
