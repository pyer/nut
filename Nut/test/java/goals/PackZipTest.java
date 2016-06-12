package nut.goals;

import nut.logging.Log;

import nut.model.Build;
import nut.model.Model;
import nut.project.Project;

import java.io.File;
import java.io.Reader;
import java.util.Properties;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class PackZipTest
{
    private final String LOCAL_REPO = "target/repository";
    private Model model = new Model();
    private Project project;

    @BeforeMethod
    private void before()
    {
        project = new Project(model);
        model.addProperty( "nut.home", LOCAL_REPO );
        model.addProperty( "basedir", "." );
        new File( LOCAL_REPO ).mkdir();
        Build build = new Build();
        build.setTargetDirectory( "target" );
        build.setResourceDirectory( "test/resources/zip" );
        model.setBuild( build );
        model.setGroupId( "local.group" );
        model.setArtifactId( "artifact" );
        model.setVersion( "0.0" );
        model.setPackaging( "zip" );
    }

    @AfterMethod
    private void after()
    {
    }

    @Test
    public void testBasicZipFile()
        throws Exception
    {
        Properties config = new Properties();
        PackZip.execute(project,config);
        File packedArtifact = new File( "target/artifact.zip" );
        assertTrue( packedArtifact.exists() );
    }

}
