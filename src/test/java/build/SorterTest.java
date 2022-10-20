package nut.build;

import nut.build.CycleDetectedException;
import nut.build.DuplicateProjectException;
import nut.model.Project;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SorterTest
{

    @Test
    public void testHashCodeNullSafe() throws CycleDetectedException, DuplicateProjectException
    {
        List<Project> projects = new ArrayList<Project>();
        new Sorter(projects).hashCode();
    }

    @Test
    public void testEqualsNullSafe() throws CycleDetectedException, DuplicateProjectException
    {
        List<Project> projects = new ArrayList<Project>();
        assertNotNull( new Sorter(projects) );
    }

    @Test
    public void testEqualsIdentity() throws CycleDetectedException, DuplicateProjectException
    {
        List<Project> projects = new ArrayList<Project>();
        Sorter thing = new Sorter(projects);
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe() throws CycleDetectedException, DuplicateProjectException
    {
        List<Project> projects = new ArrayList<Project>();
        assertNotNull( new Sorter(projects).toString() );
    }

    @Test
    public void testEmptyProject() throws CycleDetectedException, DuplicateProjectException
    {
        List<Project> projects = new ArrayList<Project>();
        Sorter sorter = new Sorter(projects);
        assertTrue( sorter.getSortedProjects().isEmpty() );
        assertFalse( sorter.hasMultipleProjects() );
    }

    @Test
    public void testMultipleProject() throws CycleDetectedException, DuplicateProjectException
    {
        Project project1 = new Project();
        project1.setGroup("nut.group");
        project1.setName("project1");
        project1.setVersion("1.0");
        Project project2 = new Project();
        project2.setGroup("nut.group");
        project2.setName("project2");
        project2.setVersion("1.0");

        List<Project> projects = new ArrayList<Project>();
        projects.add(project1);
        projects.add(project2);
        Sorter sorter = new Sorter(projects);
        assertTrue( sorter.hasMultipleProjects() );
    }

    @Test(expectedExceptions = DuplicateProjectException.class)
    public void testDuplicateProject() throws CycleDetectedException, DuplicateProjectException
    {
        Project project = new Project();
        project.setGroup("nut.group");
        project.setName("project");
        project.setVersion("1.0");

        List<Project> projects = new ArrayList<Project>();
        projects.add(project);
        projects.add(project);
        Sorter sorter = new Sorter(projects);
    }

}
