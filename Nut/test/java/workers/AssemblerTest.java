package nut.workers;

import nut.project.Project;
import nut.workers.AssemblerException;
import nut.logging.Log;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class AssemblerTest
{
    @Test
    public void testHashCodeNullSafe() {
        new Assembler().hashCode();
    }

    @Test
    public void testDefaultPackaging()
        throws AssemblerException
    {
        Assembler assembler = new Assembler();
        String basedir = System.getProperty( "basedir" );
        Project project = assembler.build( new File(basedir + "/test/resources/project/nut.xml") );
        assertEquals( "modules", project.getModel().getPackaging() );
    }

//    @Test(enabled=false)
    @Test
    public void testProperties()
        throws AssemblerException
    {
        Assembler assembler = new Assembler();
        String basedir = System.getProperty( "basedir" );
        Project project = assembler.build( new File(basedir + "/test/resources/project/nut.xml") );
        assertEquals( "1.1", project.getVersion() );
//        assertEquals( "artifact", project.getArtifactId() );
//        assertEquals( "test.groupId", project.getGroupId() );
    }

    @Test
    public void testNullVersion()
        throws AssemblerException
    {
        Assembler assembler = new Assembler();
        String basedir = System.getProperty( "basedir" );
        Project project = assembler.build( new File(basedir + "/test/resources/nullVersion.xml") );
        assertEquals( "1.1", project.getVersion() );
    }

}
