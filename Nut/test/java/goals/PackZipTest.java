package nut.goals;

import nut.model.Layout;
import nut.model.Project;

import java.io.File;
import java.util.Properties;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class PackZipTest
{
    private final String LOCAL_REPO = "target/repository";
    private Project project;

    @BeforeMethod
    public void before()
    {
        project = new Project();
        project.addProperty( "nut.home", LOCAL_REPO );
        project.addProperty( "basedir", "." );
        new File( LOCAL_REPO ).mkdir();
        Layout layout = new Layout();
        layout.setTargetDirectory( "target" );
        layout.setResourceDirectory( "test/resources/zip" );
        project.setLayout( layout );
        project.setGroupId( "local.group" );
        project.setArtifactId( "artifact" );
        project.setVersion( "0.0" );
        project.setPackaging( "zip" );
    }

/*
    @AfterMethod
    public void after()
    {
    }
*/
    @Test
    public void testBasicZipFile() throws Exception
    {
        new PackZip().execute(project);
        File packedArtifact = new File( "target/artifact.zip" );
        assertTrue( packedArtifact.exists() );
    }

}
