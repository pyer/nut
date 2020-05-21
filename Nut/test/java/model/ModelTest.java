package nut.model;

/*
import java.util.ArrayList;
import java.util.Arrays;
*/
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ModelTest
{

    @Test
    public void testHashCodeNullSafe()
    {
        new Model().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertNotNull( new Model() );
    }

    @Test
    public void testEqualsIdentity()
    {
        Model thing = new Model();
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Model().toString() );
    }

    @Test
    public void testModelId()
    {
        Model model = new Model();
        model.setGroupId("group");
        model.setArtifactId("artifact");
        model.setVersion("1.0");
        assertEquals( model.getId(), "group:artifact:1.0" );
    }

    @Test
    public void testModelDefaultPackaging()
    {
        Model model = new Model();
        assertEquals( model.getPackaging(), "modules" );
    }

    @Test
    public void testModelPackaging()
    {
        Model model = new Model();
        model.setPackaging("zip");
        assertEquals( model.getPackaging(), "zip" );
    }

/*
    @Test
    public void testValidate() throws ValidationException
    {
        Model model = new Model();
        model.validate();
    }
*/
}
/*

    public void validate() throws ValidationException
    {
        String ID_REGEX = "[A-Za-z0-9_\\-.]+";
        validateStringNotEmpty( "groupId", groupId );
        if ( !groupId.matches( ID_REGEX ) )
            throw new ValidationException( "groupId '" + groupId + "' does not match a valid id pattern." );
        validateStringNotEmpty( "artifactId", artifactId );
        if ( !artifactId.matches( ID_REGEX ) )
            throw new ValidationException( "artifactId '" + artifactId + "' does not match a valid id pattern." );

        validateStringNotEmpty( "version", version );
        validateStringNotEmpty( "packaging", packaging );

        if ( !getModules().isEmpty() && !"modules".equals( packaging ) ) {
            throw new ValidationException( "Packaging '" + packaging +
                                                "' is invalid. Aggregator projects require 'modules' as packaging." );
        }

        for ( Iterator it = getGoals().iterator(); it.hasNext(); )
        {
            Goal g = (Goal) it.next();
            g.validate();
        }

        for ( Iterator it = getDependencies().iterator(); it.hasNext(); )
        {
            Dependency dep = (Dependency) it.next();
            dep.validate(version);
        }

        for ( Iterator it = getRepositories().iterator(); it.hasNext(); )
        {
            Repository repo = (Repository) it.next();
            repo.validate();
        }

        if ( build != null ) {
            build.validate();
        }
    }

    private void validateStringNotEmpty( String fieldName, String string )
        throws ValidationException
    {
        if ( string == null )
            throw new ValidationException( "'" + fieldName + "' is null." );
        if ( string.length() <1 )
            throw new ValidationException( "'" + fieldName + "' is empty." );
    }

}





    @Test
    public void testValidateGoodGoal() throws ValidationException
    {
        Model model = new Model();
        Goal goal = new Goal();
        ArrayList<Goal> goals = new ArrayList<Goal>(Arrays.asList(goal));
        model.setGoals( goals );
        try {
          ProjectBuilder builder = new ProjectBuilder();
          Project project = builder.build( new File("test/resources/goodGoal.xml") );
          project.getBuild().validate();
        }
        catch ( BuildException e ) {
            throw new ValidationException( "Build failure" );
        }
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testValidateBadGoal() throws ValidationException
    {
        try {
          ProjectBuilder builder = new ProjectBuilder();
          Project project = builder.build( new File("test/resources/badGoal.xml") );
          project.getBuild().validate();
        }
        catch ( BuildException e ) {
            throw new ValidationException( "Build failure" );
        }
    }
*/
