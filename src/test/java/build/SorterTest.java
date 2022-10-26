package nut.build;

import nut.build.Sorter;
import nut.build.SorterException;
import nut.model.Dependency;
import nut.model.Project;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class SorterTest
{

    @Test
    public void testHashCodeNullSafe() throws SorterException
    {
        List<Project> projects = new ArrayList<Project>();
        new Sorter(projects).hashCode();
    }

    @Test
    public void testEqualsNullSafe() throws SorterException
    {
        List<Project> projects = new ArrayList<Project>();
        assertNotNull( new Sorter(projects) );
    }

    @Test
    public void testEqualsIdentity() throws SorterException
    {
        List<Project> projects = new ArrayList<Project>();
        Sorter thing = new Sorter(projects);
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe() throws SorterException
    {
        List<Project> projects = new ArrayList<Project>();
        assertNotNull( new Sorter(projects).toString() );
    }

    @Test
    public void testEmptyProject() throws SorterException
    {
        List<Project> projects = new ArrayList<Project>();
        Sorter sorter = new Sorter(projects);
        sorter.sortProjects();

        assertTrue( sorter.getSortedProjects().isEmpty() );
        assertFalse( sorter.hasMultipleProjects() );
    }

    @Test
    public void testMultiProject() throws SorterException
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
        sorter.sortProjects();

        assertFalse( sorter.getSortedProjects().isEmpty() );
        assertTrue( sorter.hasMultipleProjects() );

        List<Project> sortedList = sorter.getSortedProjects();
        assertEquals( sortedList.get(0), project1 );
        assertEquals( sortedList.get(1), project2 );
    }

    /*
     * Test method checkDuplicate
     */
    @Test
    public void testNoDuplicateProject() throws SorterException
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
        sorter.checkDuplicate();
    }

    @Test(expectedExceptions = SorterException.class)
    public void testDuplicateProject() throws SorterException
    {
        Project project = new Project();
        project.setGroup("nut.group");
        project.setName("project");
        project.setVersion("1.0");

        List<Project> projects = new ArrayList<Project>();
        projects.add(project);
        projects.add(project);
        Sorter sorter = new Sorter(projects);
        sorter.checkDuplicate();
    }


    /*
     * Test method checkCyclicDependency
     */
    @Test
    public void testNoCycle1()
    {
        // No cycle
        // a --> b --->c
        Project project_a = new Project("a");
        Project project_b = new Project("b");
        Project project_c = new Project("c");

        assertEquals( project_b.getPath(), "//b-.jar" );
        project_a.getDependencies().add(new Dependency("//b-.jar"));

        assertEquals( project_c.getPath(), "//c-.jar" );
        project_b.getDependencies().add(new Dependency("//c-.jar"));

        List<Project> projects = new ArrayList<Project>();
        projects.add(project_a);
        projects.add(project_b);
        Sorter sorter = new Sorter(projects);
        try {
            sorter.checkCyclicDependency();
        } catch ( SorterException e ) {
            fail( "Cycle should not be detected" );
        }
    }

    @Test
    public void testNoCycle2()
    {
        // | --> c
        // a --> b
        // | | --> d
        // --------->
        Project project_a = new Project("a");
        Project project_b = new Project("b");
        Project project_c = new Project("c");
        Project project_d = new Project("d");

        project_a.getDependencies().add(new Dependency("//b-.jar"));
        project_b.getDependencies().add(new Dependency("//c-.jar"));
        project_b.getDependencies().add(new Dependency("//d-.jar"));
        project_a.getDependencies().add(new Dependency("//d-.jar"));

        List<Project> projects = new ArrayList<Project>();
        projects.add(project_a);
        projects.add(project_b);
        projects.add(project_c);
        projects.add(project_d);
        Sorter sorter = new Sorter(projects);
        try {
            sorter.checkCyclicDependency();
        } catch ( SorterException e ) {
            fail( "Cycle should not be detected" );
        }
    }

    @Test(expectedExceptions = SorterException.class)
    public void testCycle1() throws SorterException
    {
        //
        // a --> b --->c
        // ^ |
        // | |
        // -----------|
        Project project_a = new Project("a");
        Project project_b = new Project("b");
        Project project_c = new Project("c");

        project_a.getDependencies().add(new Dependency("//b-.jar"));
        project_b.getDependencies().add(new Dependency("//c-.jar"));
        project_c.getDependencies().add(new Dependency("//a-.jar"));

        List<Project> projects = new ArrayList<Project>();
        projects.add(project_a);
        projects.add(project_b);
        projects.add(project_c);
        Sorter sorter = new Sorter(projects);
        sorter.checkCyclicDependency();
    }

    @Test(expectedExceptions = SorterException.class)
    public void testCycle2() throws SorterException
    {
        // ------------
        // | |
        // V | --> c
        // a --> b
        // | | --> d
        // --------->
        Project project_a = new Project("a");
        Project project_b = new Project("b");
        Project project_c = new Project("c");
        Project project_d = new Project("d");

        project_a.getDependencies().add(new Dependency("//b-.jar"));
        project_b.getDependencies().add(new Dependency("//c-.jar"));
        project_b.getDependencies().add(new Dependency("//d-.jar"));
        project_a.getDependencies().add(new Dependency("//d-.jar"));
        project_c.getDependencies().add(new Dependency("//a-.jar"));

        List<Project> projects = new ArrayList<Project>();
        projects.add(project_a);
        projects.add(project_b);
        projects.add(project_c);
        projects.add(project_d);
        Sorter sorter = new Sorter(projects);
        sorter.checkCyclicDependency();
    }

    @Test(expectedExceptions = SorterException.class)
    public void testCycle3() throws SorterException
    {
        // f --> g --> h
        // |
        // |
        // a --> b ---> c --> d
        // ^ |
        // | V
        // ------------ e
        Project project_a = new Project("a");
        Project project_b = new Project("b");
        Project project_c = new Project("c");
        Project project_d = new Project("d");
        Project project_e = new Project("e");
        Project project_f = new Project("f");
        Project project_g = new Project("g");
        Project project_h = new Project("h");

        project_a.getDependencies().add(new Dependency("//b-.jar"));
        project_b.getDependencies().add(new Dependency("//c-.jar"));
        project_b.getDependencies().add(new Dependency("//f-.jar"));
        project_f.getDependencies().add(new Dependency("//g-.jar"));
        project_g.getDependencies().add(new Dependency("//h-.jar"));
        project_c.getDependencies().add(new Dependency("//d-.jar"));
        project_d.getDependencies().add(new Dependency("//e-.jar"));
        project_e.getDependencies().add(new Dependency("//b-.jar"));

        List<Project> projects = new ArrayList<Project>();
        projects.add(project_a);
        projects.add(project_b);
        projects.add(project_c);
        projects.add(project_d);
        projects.add(project_e);
        projects.add(project_f);
        projects.add(project_g);
        projects.add(project_h);
        Sorter sorter = new Sorter(projects);
        sorter.checkCyclicDependency();
    }

}

