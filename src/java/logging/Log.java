package nut.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

/**
 * Logger with "standard" output and error output stream.
 */

public class Log
{
    private static boolean debug = false;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    public void debugOn()
    {
        debug = true;
    }

    public void debugOff()
    {
        debug = false;
    }

    public void debug( CharSequence content )
    {
      if(debug)
        print( "debug", content );
    }

    public void debug( CharSequence content, Throwable error )
    {
      if(debug)
        print( "debug", content, error );
    }

    public void debug( Throwable error )
    {
      if(debug)
        print( "debug", error );
    }

    public void info( CharSequence content )
    {
        print( "info", content );
    }

    public void info( CharSequence content, Throwable error )
    {
        print( "info", content, error );
    }

    public void info( Throwable error )
    {
        print( "info", error );
    }

    public void warn( CharSequence content )
    {
        System.out.print( "\033[1;33m" );
        print( "warn", content );
        System.out.print( "\033[1;37m" );
    }

    public void warn( CharSequence content, Throwable error )
    {
        System.out.print( "\033[1;33m" );
        print( "warn", content, error );
        System.out.print( "\033[1;37m" );
    }

    public void warn( Throwable error )
    {
        System.out.print( "\033[1;33m" );
        print( "warn", error );
        System.out.print( "\033[1;37m" );
    }

    public void error( CharSequence content )
    {
        System.err.print( "\033[1;31m" );
        System.err.println( "[error] " + content.toString() );
        System.err.print( "\033[1;37m" );
    }

    public void error( CharSequence content, Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        System.err.print( "\033[1;31m" );
        System.err.println( "[error] " + content.toString() + "\n\n" + sWriter.toString() );
        System.err.print( "\033[0;37m" );
    }

    public void error( Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        System.err.print( "\033[1;31m" );
        System.err.println( "[error] " + sWriter.toString() );
        System.err.print( "\033[0;37m" );
    }

    public void line()
    {
        info( "------------------------------------------------------------------------" );
    }

    // ----------------------------------------------------------------------
    // Nut log methods
    // ----------------------------------------------------------------------

    public void build( CharSequence content )
    {
        System.out.print( "\033[1;32m" );
        print( "BUILD  ", content );
        System.out.print( "\033[1;37m" );
    }

    public void success( CharSequence content )
    {
        System.out.print( "\033[1;32m" );
        print( "SUCCESS", content );
        System.out.print( "\033[1;37m" );
    }

    public void success( long time )
    {
        System.out.print( "\033[1;32m" );
        print( "SUCCESS", "Done in " + getFormattedTime( time ) );
        System.out.print( "\033[1;37m" );
    }

    public void warning( CharSequence content )
    {
        System.err.print( "\033[1;33m" );
        print( "WARNING", content );
        System.err.print( "\033[1;37m" );
    }

    public void failure( CharSequence content )
    {
        System.err.print( "\033[1;31m" );
        System.err.println( "[FAILURE] " + content.toString() );
        System.err.print( "\033[1;37m" );
    }

    public void failure( Exception e )
    {
        System.err.print( "\033[1;31m" );
        System.err.println( "[FAILURE] " + e.getMessage() );
        System.err.print( "\033[1;37m" );
        if(debug)
           e.printStackTrace();
    }

    public void fatal( Throwable t )
    {
        System.err.print( "\033[1;31m" );
        System.err.println( "[FATAL] " + t.getMessage() );
        System.err.print( "\033[1;37m" );
        if(debug)
           t.printStackTrace();
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    private static String getFormattedTime( long time )
    {
        String pattern = "s.SSS's'";
        if ( time / 60000L > 0 )
        {
            pattern = "m:s" + pattern;
            if ( time / 3600000L > 0 )
            {
                pattern = "H:m" + pattern;
            }
        }
        DateFormat fmt = new SimpleDateFormat( pattern );
        fmt.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        return fmt.format( new Date( time ) );
    }

    private void print( String content )
    {
        System.out.print( content );
    }

    private void print( String prefix, CharSequence content )
    {
        System.out.println( "[" + prefix + "] " + content.toString() );
    }

    private void print( String prefix, Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        System.out.println( "[" + prefix + "] " + sWriter.toString() );
    }

    private void print( String prefix, CharSequence content, Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        System.out.println( "[" + prefix + "] " + content.toString() + "\n\n" + sWriter.toString() );
    }
}
