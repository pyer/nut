package nut.interpolation;

import nut.model.Project;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

public class InterpolatorTest
{

    @Test
    public void testDefaultProject()
    {
        Interpolator i = new Interpolator();
        Project project = new Project();
        assertEquals( project, i.interpolatedProject( project ) );
    }

}
