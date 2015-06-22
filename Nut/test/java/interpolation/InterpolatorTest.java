package nut.interpolation;

import nut.model.Model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class InterpolatorTest
{

    @Test
    public void testDefaultModel()
    {
        Interpolator i = new Interpolator();
        Model m = new Model();
        assertEquals( m, i.interpolatedModel( m ) );
    }

}
