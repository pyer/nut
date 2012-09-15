package nut.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Logger with "standard" output and error output stream.
 */
public class Log
{
    private boolean debug = false;
    public void debugOn()
    {
        debug = true;
    }
    public void debugOff()
    {
        debug = false;
    }


    /**
     * @see org.apache.nut.plugin.logging.Log#debug(java.lang.CharSequence)
     */
    public void debug( CharSequence content )
    {
      if(debug)
        print( "debug", content );
    }

    /**
     * @see org.apache.nut.plugin.logging.Log#debug(java.lang.CharSequence, java.lang.Throwable)
     */
    public void debug( CharSequence content, Throwable error )
    {
      if(debug)
        print( "debug", content, error );
    }

    /**
     * @see org.apache.nut.plugin.logging.Log#debug(java.lang.Throwable)
     */
    public void debug( Throwable error )
    {
      if(debug)
        print( "debug", error );
    }

    /**
     * @see org.apache.nut.plugin.logging.Log#info(java.lang.CharSequence)
     */
    public void info( CharSequence content )
    {
        print( "info", content );
    }

    /**
     * @see org.apache.nut.plugin.logging.Log#info(java.lang.CharSequence, java.lang.Throwable)
     */
    public void info( CharSequence content, Throwable error )
    {
        print( "info", content, error );
    }

    /**
     * @see org.apache.nut.plugin.logging.Log#info(java.lang.Throwable)
     */
    public void info( Throwable error )
    {
        print( "info", error );
    }

    /**
     * @see org.apache.nut.plugin.logging.Log#warn(java.lang.CharSequence)
     */
    public void warn( CharSequence content )
    {
        System.out.print( "\033[1;33m" );
        print( "warn", content );
        System.out.print( "\033[1;37m" );
    }

    /**
     * @see org.apache.nut.plugin.logging.Log#warn(java.lang.CharSequence, java.lang.Throwable)
     */
    public void warn( CharSequence content, Throwable error )
    {
        System.out.print( "\033[1;33m" );
        print( "warn", content, error );
        System.out.print( "\033[1;37m" );
    }

    /**
     * @see org.apache.nut.plugin.logging.Log#warn(java.lang.Throwable)
     */
    public void warn( Throwable error )
    {
        System.out.print( "\033[1;33m" );
        print( "warn", error );
        System.out.print( "\033[1;37m" );
    }

    /**
     * @see org.apache.nut.plugin.logging.Log#error(java.lang.CharSequence)
     */
    public void error( CharSequence content )
    {
        debug = true;
        System.err.print( "\033[1;31m" );
        System.err.println( "[error] " + content.toString() );
        System.err.print( "\033[1;37m" );
    }

    /**
     * @see org.apache.nut.plugin.logging.Log#error(java.lang.CharSequence, java.lang.Throwable)
     */
    public void error( CharSequence content, Throwable error )
    {
        debug = true;
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        System.err.print( "\033[1;31m" );
        System.err.println( "[error] " + content.toString() + "\n\n" + sWriter.toString() );
        System.err.print( "\033[0;37m" );
    }

    /**
     * @see org.apache.nut.plugin.logging.Log#error(java.lang.Throwable)
     */
    public void error( Throwable error )
    {
        debug = true;
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        System.err.print( "\033[1;31m" );
        System.err.println( "[error] " + sWriter.toString() );
        System.err.print( "\033[0;37m" );
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
