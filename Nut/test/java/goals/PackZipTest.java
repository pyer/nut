package nut.goals;

import nut.model.Layout;
import nut.model.Model;
import nut.project.Project;

import java.io.File;
import java.util.Properties;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class PackZipTest
{
    private final String LOCAL_REPO = "target/repository";
    private Model model = new Model();
    private Project project = new Project();

    @BeforeMethod
    public void before()
    {
        model.addProperty( "nut.home", LOCAL_REPO );
        model.addProperty( "basedir", "." );
        new File( LOCAL_REPO ).mkdir();
        Layout layout = new Layout();
        layout.setTargetDirectory( "target" );
        layout.setResourceDirectory( "test/resources/zip" );
        model.setLayout( layout );
        model.setGroupId( "local.group" );
        model.setArtifactId( "artifact" );
        model.setVersion( "0.0" );
        model.setPackaging( "zip" );
        project.setModel(model);
    }

/*
    @AfterMethod
    public void after()
    {
    }
*/
    @Test
    public void testBasicZipFile()
        throws Exception
    {
        PackZip.execute(project);
        File packedArtifact = new File( "target/artifact.zip" );
        assertTrue( packedArtifact.exists() );
    }

}
