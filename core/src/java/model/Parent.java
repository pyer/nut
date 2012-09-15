package nut.model;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Date;

/**
 * 
 *         
 *         The <code>&lt;parent&gt;</code> element contains
 * informations required to the parent project.
 *         
 *       
 * 
 * @version $Revision$ $Date$
 */
public class Parent implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The group id of the parent project to inherit from.
     */
    private String groupId;

    /**
     * The artifact id of the parent project to inherit from.
     */
    private String artifactId;

    /**
     * The version of the parent project to inherit.
     */
    private String version;

      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Get the group id of the parent project to inherit from.
     * 
     * @return String
     */
    public String getGroupId()
    {
        return this.groupId;
    } //-- String getGroupId() 

    /**
     * Get the artifact id of the parent project to inherit from.
     * 
     * @return String
     */
    public String getArtifactId()
    {
        return this.artifactId;
    } //-- String getArtifactId() 

    /**
     * Get the version of the parent project to inherit.
     * 
     * @return String
     */
    public String getVersion()
    {
        return this.version;
    } //-- String getVersion() 

    /**
     * Set the group id of the parent project to inherit from.
     * 
     * @param groupId
     */
    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    } //-- void setGroupId( String ) 

    /**
     * Set the artifact id of the parent project to inherit from.
     * 
     * @param artifactId
     */
    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    } //-- void setArtifactId( String ) 

    /**
     * Set the version of the parent project to inherit.
     * 
     * @param version
     */
    public void setVersion( String version )
    {
        this.version = version;
    } //-- void setVersion( String ) 


            
    /**
     * @return the id as <code>groupId:artifactId:version</code>
     */
    public String getId()
    {
        StringBuffer id = new StringBuffer();

        id.append( getGroupId() );
        id.append( ":" );
        id.append( getArtifactId() );
        id.append( ":" );
        id.append( getVersion() );
//System.out.println( "Parent: " + id.toString() );

        return id.toString();
    }
            
          
    private String modelEncoding = "UTF-8";

    /**
     * Set an encoding used for reading/writing the model.
     *
     * @param modelEncoding the encoding used when reading/writing the model.
     */
    public void setModelEncoding( String modelEncoding )
    {
        this.modelEncoding = modelEncoding;
    }

    /**
     * @return the current encoding used when reading/writing this model.
     */
    public String getModelEncoding()
    {
        return modelEncoding;
    }
}
