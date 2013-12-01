package nut.execution;

import nut.logging.Log;

import nut.artifact.Artifact;
import nut.artifact.InvalidArtifactRTException;

import nut.model.Plugin;

import nut.project.NutProject;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PluginManager
{
    private Map buildFailuresByProject = new HashMap();
    private Map pluginContextsByProjectAndPluginKey = new HashMap();
    private Map buildSuccessesByProject = new HashMap();
    private NutProject project;
    private Log log;

    public PluginManager( NutProject project, Log log )
    {
        this.project = project;
        this.log = log;
    }
    
    public void executeProject( List goals )
        throws BuildFailureException
    {
        log.info( "Building " + project.getName() );
        log.debug( "[" + project.getId() + "]");
        for ( Iterator it = goals.iterator(); it.hasNext(); )
        {
            String goal = (String)it.next();
            //log.debug( "* execute goal " + goal );
            int index = goal.indexOf(':');
            if ( index>0 )
            {
                Plugin plugin = new Plugin();
                // plugin.setGroupId(  ); default groupId is good
                plugin.setArtifactId( goal.substring(0,index) );
                plugin.setVersion( goal.substring(index+1,goal.length()) );

                //log.info( "["+goal.substring(0,index)+"]" );
                //log.info( "["+goal.substring(index+1,goal.length())+"]" );
                //log.debug( plugin.toString() );
                executePlugin( plugin, project );
            }
            else
            {
                if ( project.getModel().getBuild() != null )
                {
                    List plugins = project.getBuild().getPlugins();
                    executePlugins( plugins, goal );
                }
            }
        }
    }

    private void executePlugins( List plugins, String goal )
        throws BuildFailureException
    {
        //log.debug("executePlugins:" + plugins);
        if ( plugins == null )
        {
            log.warn("No plugin");
            return;
        }

        boolean run = false;
        for ( Iterator it = plugins.iterator(); it.hasNext(); )
        {
            Plugin plugin = (Plugin) it.next();
            if ( goal.equals( plugin.getGoal() ) || (goal.equals( "build" ) && !plugin.getSkip()) )
            {
                executePlugin( plugin, project );
                project.buildIsDone();
                run = true;
            }
        }
        if ( run == false && !project.getModel().getPackaging().equals("modules") )
        {
           // throw new BuildFailureException( "No plugin for " + goal + " goal");
           log.warn( "No plugin for goal '" + goal + "'");
        }
    }

    @SuppressWarnings("unchecked")
    private void executePlugin( Plugin plugin, NutProject project )
        throws BuildFailureException
    {
        try
        {
               log.debug( plugin.toString() );
               project.getBuild().setCurrentPlugin( plugin );
               // just to see the id of the plugin
               Artifact artifact = new Artifact( plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion(), "jar", null );
               String repository = System.getProperty( "nut.home" );
               File file = new File( repository+File.separator+artifact.getPath() );
               log.debug( "File: " + file.getAbsolutePath() );
               if ( file.exists() )
               {
                   ClassLoader mainLoader = Thread.currentThread().getContextClassLoader();
                   NutClassRealm realm    = new NutClassRealm( mainLoader, file.toURI().toURL() );
                   Class mainClass        = realm.loading( "nut.plugins." + artifact.getArtifactId() );
                   Method mainMethod = mainClass.getMethod( "execute", new Class[] { NutProject.class, Log.class } );
                   //Method mainMethod = mainClass.getMethod( "execute", new Class[] { NutProject.class } );
                   mainMethod.invoke( mainClass, new Object[]{project,log} );
                   //mainMethod.invoke( mainClass, new Object[]{project} );
               }
               else
               {
                   throw new BuildFailureException( plugin + " not found");
               }
        }
        catch ( InvalidArtifactRTException e )
        {
                throw new BuildFailureException( e.getMessage() , e );
        }
        catch ( InvocationTargetException e )
        {
                throw new BuildFailureException( plugin + "!", e );
        }
        catch ( Exception e )
        {
                throw new BuildFailureException( "exception", e );
        }
    }
}
