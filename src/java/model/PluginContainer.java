package nut.model;

import nut.model.Plugin;
  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

//import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the plugins informations for the project.
 * 
 * @version $Revision$ $Date$
 */
public class PluginContainer implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field plugins.
     */
    private List<Plugin> plugins;


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addPlugin.
     * 
     * @param plugin
     */
    public void addPlugin( Plugin plugin )
    {
        if ( !(plugin instanceof Plugin) )
        {
            throw new ClassCastException( "PluginContainer.addPlugins(plugin) parameter must be instanceof " + Plugin.class.getName() );
        }
        getPlugins().add( plugin );
    } //-- void addPlugin( Plugin ) 

    /**
     * Method getPlugins.
     * 
     * @return List
     */
    public List<Plugin> getPlugins()
    {
        if ( this.plugins == null )
        {
            this.plugins = new ArrayList<Plugin>();
        }
    
        return this.plugins;
    } //-- List getPlugins() 

    /**
     * Method removePlugin.
     * 
     * @param plugin
     */
    public void removePlugin( Plugin plugin )
    {
        if ( !(plugin instanceof Plugin) )
        {
            throw new ClassCastException( "PluginContainer.removePlugins(plugin) parameter must be instanceof " + Plugin.class.getName() );
        }
        getPlugins().remove( plugin );
    } //-- void removePlugin( Plugin ) 

    /**
     * Set the list of plugins to use.
     * 
     * @param plugins
     */
    public void setPlugins( List<Plugin> plugins )
    {
        this.plugins = plugins;
    } //-- void setPlugins( List ) 

}
