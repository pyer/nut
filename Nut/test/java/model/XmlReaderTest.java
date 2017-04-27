package nut.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.StringReader;

import nut.xml.XmlParserException;

public class XmlReaderTest
{

    @Test
    public void testHashCodeNullSafe()
    {
        new XmlReader().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertNotNull( new XmlReader() );
    }

    @Test
    public void testEqualsIdentity()
    {
        XmlReader thing = new XmlReader();
        assertTrue( thing.equals( thing ) );
    }

    @Test(expectedExceptions = XmlParserException.class)
    public void testEmptyXmlFile() throws IOException, XmlParserException
    {
        XmlReader xml = new XmlReader();
        Model model = xml.parseModel( new StringReader("") );
    }

    @Test(expectedExceptions = XmlParserException.class)
    public void testXmlDeclOnly() throws IOException, XmlParserException
    {
        XmlReader xml = new XmlReader();
        Model model = xml.parseModel( new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>") );
    }

    @Test
    public void testEmptyProject() throws IOException, XmlParserException
    {
        XmlReader xml = new XmlReader();
        Model model = xml.parseModel( new StringReader("<project></project>") );
        assertNotNull( model );
    }

    @Test
    public void testEmptyProjectWithXmlDecl() throws IOException, XmlParserException
    {
        XmlReader xml = new XmlReader();
        Model model = xml.parseModel( new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><project></project>") );
        assertNotNull( model );
    }

    @Test
    public void testProjectwithEndOfline() throws IOException, XmlParserException
    {
        XmlReader xml = new XmlReader();
        Model model = xml.parseModel( new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<project>\n<groupId>nut</groupId>\n<artifactId>Test</artifactId>\n<packaging>jar</packaging>\n<version>1.0</version>\n</project>") );
        assertNotNull( model );
    }

    @Test
    public void testProjectwithXmlDecl() throws IOException, XmlParserException
    {
        XmlReader xml = new XmlReader();
        Model model = xml.parseModel( new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><project><groupId>nut</groupId><artifactId>Test</artifactId><packaging>jar</packaging><version>1.0</version></project>") );
        assertNotNull( model );
    }

    @Test
    public void testProjectwithoutXmlDecl() throws IOException, XmlParserException
    {
        XmlReader xml = new XmlReader();
        Model model = xml.parseModel( new StringReader("<project><groupId>nut</groupId><artifactId>Test</artifactId><packaging>jar</packaging><version>1.0</version></project>") );
        assertNotNull( model );
    }

}
