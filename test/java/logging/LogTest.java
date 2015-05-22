package nut.logging;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class LogTest
{
    @Test
    public void testHashCodeNullSafe() {
        new Log().hashCode();
    }

    @Test
    public void testEqualsNullSafe() {
        assertFalse( new Log().equals( null ) );
    }

    @Test
    public void testEqualsIdentity() {
        Log log = new Log();
        assertTrue( log.equals( log ) );
    }

    @Test
    public void testDebugOn() {
        PrintStream original = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Log log = new Log();
        log.debugOn();
        log.debug( "hello" );
        String out = outContent.toString();
        log.debugOff();
        System.setOut(original);
        assertEquals("[debug] hello\n", out);
    }

    @Test
    public void testDebugOff() {
        PrintStream original = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Log log = new Log();
        // log.debugOff();
        // default debug is off
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
        new Log().error( "ERROR" );
        String out = outContent.toString();
        System.setErr(original);
        assertEquals("\033[1;31m[error] ERROR\n\033[1;37m", out);
    }

    @Test
    public void testWarn() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(outContent));
        new Log().warn( "WARNING" );
        String out = outContent.toString();
        System.setOut(original);
        assertEquals("\033[1;33m[warn] WARNING\n\033[1;37m", out);
    }

    @Test
    public void testInfo() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(outContent));
        new Log().info( "Hello !" );
        String out = outContent.toString();
        System.setOut(original);
        assertEquals("[info] Hello !\n", out);
    }

}