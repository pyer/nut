package nut.build;

import nut.annotations.Test;
import nut.model.Project;
import nut.model.ParserException;
import nut.model.ValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static nut.Assert.assertEquals;
import static nut.Assert.assertNull;
import static nut.Assert.assertNotNull;
import static nut.Assert.assertTrue;
import static nut.Assert.assertFalse;

public class ScannerTest
{
    private String nutFileName() {
        String basedir = System.getProperty("nut.basedir", ".");
        return basedir + "/test/resources/fullProject.yaml";
    }

    @Test
    public void testHashCodeNullSafe() throws IOException, ParserException, ValidationException
    {
        new Scanner(nutFileName()).hashCode();
    }

    @Test
    public void testEqualsNullSafe() throws IOException, ParserException, ValidationException
    {
        assertNotNull( new Scanner(nutFileName()) );
    }

    @Test
    public void testEqualsIdentity() throws IOException, ParserException, ValidationException
    {
        Scanner thing = new Scanner(nutFileName());
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe() throws IOException, ParserException, ValidationException
    {
        assertNotNull( new Scanner(nutFileName()).toString() );
    }

    @Test(expectedExceptions = ParserException.class)
    public void testNullProject() throws IOException, ParserException, ValidationException
    {
        Scanner scanner = new Scanner("noProject.yaml");
        assertNull( scanner.getProjects() );
    }

    @Test
    public void testFullProject() throws IOException, ParserException, ValidationException
    {
        List<Project> projects = new ArrayList<Project>();
        Project project = new Project();
        project.setGroup("nut.test");
        project.setName("full");
        project.setVersion("3.0");
        projects.add(project);
        Scanner scanner = new Scanner(nutFileName());
        assertEquals( projects, scanner.getProjects() );
    }

}
