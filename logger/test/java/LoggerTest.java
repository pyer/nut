package nut;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import nut.annotations.Test;

import static nut.Assert.*;

/*
Logger.java public methods:
    public void debugOn()
    public void debugOff()
    public void debug( CharSequence content )
    public void debug( CharSequence content, Throwable error )
    public void debug( Throwable error )
    public void info( CharSequence content )
    public void info( CharSequence content, Throwable error )
    public void info( Throwable error )
    public void warn( CharSequence content )
    public void warn( CharSequence content, Throwable error )
    public void warn( Throwable error )
    public void error( CharSequence content )
    public void error( CharSequence content, Throwable error )
    public void error( Throwable error )
    public void line()
*/

public class LoggerTest
{
    @Test
    public void testHashCodeNullSafe() {
        new Logger().hashCode();
    }

    @Test
    public void testEqualsNullSafe() {
        assertNotNull( new Logger() );
    }

    @Test
    public void testEqualsIdentity() {
        Logger log = new Logger();
        assertTrue( log.equals( log ) );
    }

    @Test
    public void testOut() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(outContent));
        new Logger().out( "hello" );
        String out = outContent.toString();
        System.setOut(original);
        assertEquals("hello\n", out);
    }

    @Test
    public void testErr() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream original = System.err;
        System.setErr(new PrintStream(outContent));
        new Logger().err( "hello" );
        String out = outContent.toString();
        System.setErr(original);
        assertEquals("hello\n", out);
    }


    @Test(enabled=false)
    public void testDebugOn() {
        PrintStream original = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Logger log = new Logger();
        log.debugOn();
        log.debug( "hello" );
        String out = outContent.toString();
        log.debugOff();
        System.setOut(original);
        assertEquals("[ debug ] hello\n", out);
    }

    @Test(enabled=false)
    public void testDebugOff() {
        PrintStream original = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Logger log = new Logger();
        log.debugOff();
        log.debug( "hello" );
        String out = outContent.toString();
        System.setOut(original);
        assertTrue(out.isEmpty());
    }

    @Test
    public void testError() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream original = System.err;
        System.setErr(new PrintStream(outContent));
        new Logger().error( "ERROR" );
        String out = outContent.toString();
        System.setErr(original);
        assertEquals("\033[1;31m[ error ] ERROR\n\033[1;37m", out);
    }

    @Test
    public void testWarn() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(outContent));
        new Logger().warn( "WARNING" );
        String out = outContent.toString();
        System.setOut(original);
        assertEquals("\033[1;33m[ warn  ] WARNING\n\033[1;37m", out);
    }

    @Test
    public void testInfo() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(outContent));
        new Logger().info( "Hello !" );
        String out = outContent.toString();
        System.setOut(original);
        assertEquals("[ info  ] Hello !\n", out);
    }

}
