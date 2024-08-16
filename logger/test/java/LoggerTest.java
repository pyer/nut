package nut;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import nut.annotations.Test;

import static nut.Assert.*;


public class LoggerTest
{
    private ByteArrayOutputStream output;
    private PrintStream original;

    private void setup() {
        output = new ByteArrayOutputStream();
        original = System.err;
        System.setErr(new PrintStream(output));
    }

    private void restore() {
        System.setErr(original);
    }


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
        output = new ByteArrayOutputStream();
        original = System.out;
        System.setOut(new PrintStream(output));
        new Logger().out( "hello" );
        System.setOut(original);
        assertEquals( output.toString(), "hello\n" );
    }

    @Test
    public void testErr() {
        output = new ByteArrayOutputStream();
        original = System.err;
        System.setErr(new PrintStream(output));
        new Logger().err( "hello" );
        System.setErr(original);
        assertEquals( output.toString(), "hello\n" );
    }

    @Test(enabled=false)
    public void testLevels() {
        Logger log = new Logger();
        Logger.Level currentLevel = log.getLevel();

        log.setLevel( Logger.Level.ERROR );
        assertFalse( log.isWarningEnabled() );
        assertFalse( log.isInfoEnabled() );
        assertFalse( log.isTraceEnabled() );
        assertFalse( log.isDebugEnabled() );

        log.setLevel( Logger.Level.WARNING );
        assertTrue(  log.isWarningEnabled() );
        assertFalse( log.isInfoEnabled() );
        assertFalse( log.isTraceEnabled() );
        assertFalse( log.isDebugEnabled() );

        log.setLevel( Logger.Level.INFO );
        assertTrue(  log.isWarningEnabled() );
        assertTrue(  log.isInfoEnabled() );
        assertFalse( log.isTraceEnabled() );
        assertFalse( log.isDebugEnabled() );

        log.setLevel( Logger.Level.TRACE );
        assertTrue(  log.isWarningEnabled() );
        assertTrue(  log.isInfoEnabled() );
        assertTrue(  log.isTraceEnabled() );
        assertFalse( log.isDebugEnabled() );

        log.setLevel( Logger.Level.DEBUG );
        assertTrue(  log.isWarningEnabled() );
        assertTrue(  log.isInfoEnabled() );
        assertTrue(  log.isTraceEnabled() );
        assertTrue(  log.isDebugEnabled() );

        log.setLevel( currentLevel );
        assertTrue( currentLevel == log.getLevel() );
    }

    @Test(enabled=false)
    public void testRestoreLevel() {
        Logger log = new Logger();
        Logger.Level currentLevel = log.getLevel();

        log.setLevel( Logger.Level.INFO );
        log.setLevel( Logger.Level.DEBUG );
        assertTrue( Logger.Level.DEBUG == log.getLevel() );
        log.restoreLevel();
        assertTrue( Logger.Level.INFO  == log.getLevel() );

        log.setLevel( currentLevel );
        assertTrue( currentLevel == log.getLevel() );
    }

    @Test
    public void testError() {
        setup();
        Logger log = new Logger();
        log.setLevel( Logger.Level.ERROR );
        log.error( "ERROR" );
        log.warn( "WARNING" );
        log.info( "INFO" );
        log.trace( "TRACE" );
        log.debug( "DEBUG" );
        log.restoreLevel();
        restore();
        assertEquals( output.toString(), "\033[1;31m[ error ] ERROR\n\033[1;37m" );
    }

    @Test
    public void testWarn() {
        setup();

        Logger log = new Logger();
        log.setLevel( Logger.Level.ERROR );
        log.warn( "WARNING" );
        log.restoreLevel();
        assertEquals( output.toString(),  "" );

        log.setLevel( Logger.Level.WARNING );
        log.warn( "WARNING" );
        log.restoreLevel();
        assertEquals( output.toString(), "\033[1;33m[ warn  ] WARNING\n\033[1;37m" );

        restore();
    }

    @Test
    public void testInfo() {
        setup();

        Logger log = new Logger();
        log.setLevel( Logger.Level.WARNING );
        log.info( "INFO" );
        log.restoreLevel();
        assertEquals( output.toString(), "" );

        log.setLevel( Logger.Level.INFO );
        log.info( "Hello !" );
        log.restoreLevel();
        assertEquals( output.toString(), "[ info  ] Hello !\n" );

        restore();
    }

    @Test
    public void testTrace() {
        setup();

        Logger log = new Logger();
        log.setLevel( Logger.Level.INFO );
        log.trace( "TRACE" );
        log.restoreLevel();
        log.out( output.toString() );
        assertEquals( output.toString(), "" );

        log.setLevel( Logger.Level.TRACE );
        log.info( "Hello" );
        log.trace( "world !" );
        log.restoreLevel();
        assertEquals( output.toString(), "[ info  ] Hello\n[ trace ] world !\n" );

        restore();
    }

    @Test
    public void testDebug() {
        setup();

        Logger log = new Logger();
        log.setLevel( Logger.Level.TRACE );
        log.debug( "DEBUG" );
        log.restoreLevel();
        assertEquals( output.toString(), "" );

        log.setLevel( Logger.Level.DEBUG );
        log.debug( "DEBUG" );
        log.restoreLevel();
        assertEquals( output.toString(), "[ debug ] DEBUG\n" );

        restore();
    }

}
