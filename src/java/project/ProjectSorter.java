package nut.project;

import nut.model.Build;
import nut.model.Dependency;
import nut.model.Goal;
import nut.model.Model;
import nut.project.Project;

import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.codehaus.plexus.util.dag.DAG;
import org.codehaus.plexus.util.dag.TopologicalSorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Sort projects by dependencies.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id: ProjectSorter.java 495147 2007-01-11 07:47:53Z jvanzyl $
 */
public class ProjectSorter
{
    private final DAG dag;

    private final List sortedProjects;

    /**
     * Sort a list of projects.
     * <ul>
     * <li>collect all the vertices for the projects that we want to build.</li>
     * <li>iterate through the deps of each project and if that dep is within
     * the set of projects we want to build then add an edge, otherwise throw
     * the edge away because that dependency is not within the set of projects
     * we are trying to build. we assume a closed set.</li>
     * <li>do a topo sort on the graph that remains.</li>
     * </ul>
     * @throws DuplicateProjectException if any projects are duplicated by id
     */
    public ProjectSorter( List<Project> projects )
        throws CycleDetectedException, DuplicateProjectException
    {
        dag = new DAG();

        Map<String,Project> projectMap = new HashMap<String,Project>();
        //Map projectMap = new HashMap();

        for ( Iterator i = projects.iterator(); i.hasNext(); )
        {
            Project project = (Project) i.next();

            String id = project.getId();
            if ( dag.getVertex( id ) != null )
            {
                throw new DuplicateProjectException( "Project '" + id + "' is duplicated" );
            }

            dag.addVertex( id );

            projectMap.put( id, project );
        }


        for ( Iterator i = projects.iterator(); i.hasNext(); )
        {
            Project project = (Project) i.next();

            String id = project.getId();

            for ( Iterator j = project.getDependencies().iterator(); j.hasNext(); )
            {
                Dependency dependency = (Dependency) j.next();

                String dependencyId = dependency.getId();

                if ( dag.getVertex( dependencyId ) != null )
                {
                    dag.addEdge( id, dependencyId );
                }
            }
        }

        List<Project> sortedProjects = new ArrayList<Project>();

        for ( Iterator i = TopologicalSorter.sort( dag ).iterator(); i.hasNext(); )
        {
            String id = (String) i.next();
            sortedProjects.add( projectMap.get( id ) );
        }

        this.sortedProjects = Collections.unmodifiableList( sortedProjects );
    }

    public List getSortedProjects()
    {
        return sortedProjects;
    }

    public boolean hasMultipleProjects()
    {
        return sortedProjects.size() > 1;
    }

}
