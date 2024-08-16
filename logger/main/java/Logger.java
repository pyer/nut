package nut;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

public class Logger
{
    // ----------------------------------------------------------------------
    // Log levels
    // ----------------------------------------------------------------------
    public enum Level {
      ERROR,
      WARNING,
      INFO,
      TRACE,
      DEBUG;
    }

    // ----------------------------------------------------------------------
    // Private variables
    // ----------------------------------------------------------------------

    private static Level level = Level.INFO;
    private Level savedLevel   = Level.INFO;
    private Date startDate;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    // used by help, env, etc...
    public void out( String content )
    {
        System.out.println( content );
    }

    // logs go to stderr
    public void err( String content )
    {
        System.err.println( content );
    }

    // Level managment
    public Level getLevel()
    {
        return this.level;
    }

    public void setLevel(Level level)
    {
        this.savedLevel = this.level;
        this.level = level;
    }

    public void restoreLevel()
    {
        this.level = this.savedLevel;
    }

    public boolean isWarningEnabled()
    {
        return level.ordinal() >= Level.WARNING.ordinal();
    }

    public boolean isInfoEnabled()
    {
        return level.ordinal() >= Level.INFO.ordinal();
    }

    public boolean isTraceEnabled()
    {
        return level.ordinal() >= Level.TRACE.ordinal();
    }

    public boolean isDebugEnabled()
    {
        return level.ordinal() >= Level.DEBUG.ordinal();
    }

    public void debug( CharSequence content )
    {
      if(isDebugEnabled())
        print( " debug ", content );
    }

    public void debug( CharSequence content, Throwable error )
    {
      if(isDebugEnabled())
        print( " debug ", content, error );
    }

    public void debug( Throwable error )
    {
      if(isDebugEnabled())
        print( " debug ", error );
    }

    public void trace( CharSequence content )
    {
      if(isTraceEnabled())
        print( " trace ", content );
    }

    public void trace( CharSequence content, Throwable error )
    {
      if(isTraceEnabled())
        print( " trace ", content, error );
    }

    public void trace( Throwable error )
    {
      if(isTraceEnabled())
        print( " trace ", error );
    }

    public void info( CharSequence content )
    {
      if(isInfoEnabled())
        print( " info  ", content );
    }

    public void info( CharSequence content, Throwable error )
    {
      if(isInfoEnabled())
        print( " info  ", content, error );
    }

    public void info( Throwable error )
    {
      if(isInfoEnabled())
        print( " info  ", error );
    }

    public void warn( CharSequence content )
    {
      if(isWarningEnabled()) {
        System.err.print( "\033[1;33m" );
        print( " warn  ", content );
        System.err.print( "\033[1;37m" );
      }
    }

    public void warn( CharSequence content, Throwable error )
    {
      if(isWarningEnabled()) {
        System.err.print( "\033[1;33m" );
        print( " warn  ", content, error );
        System.err.print( "\033[1;37m" );
      }
    }

    public void warn( Throwable error )
    {
      if(isWarningEnabled()) {
        System.err.print( "\033[1;33m" );
        print( " warn  ", error );
        System.err.print( "\033[1;37m" );
      }
    }

    public void error( CharSequence content )
    {
        System.err.print( "\033[1;31m" );
        System.err.println( "[ error ] " + content.toString() );
        System.err.print( "\033[1;37m" );
    }

    public void error( CharSequence content, Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        System.err.print( "\033[1;31m" );
        System.err.println( "[ error ] " + content.toString() + "\n\n" + sWriter.toString() );
        System.err.print( "\033[0;37m" );
    }

    public void error( Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        System.err.print( "\033[1;31m" );
        System.err.println( "[ error ] " + sWriter.toString() );
        System.err.print( "\033[0;37m" );
    }

    public void line()
    {
        info( "------------------------------------------------------------------------" );
    }

    // ----------------------------------------------------------------------
    // Nut log methods
    // ----------------------------------------------------------------------

    public void start()
    {
        startDate = new Date();
        line();
        info( "Started at " + startDate );
    }

    public void finish()
    {
        Date finishDate = new Date();
        long time = finishDate.getTime() - startDate.getTime();
        line();
        info( "Total time: " + formatTime( time ) );
        info( "Finished at " + finishDate );
        line();
    }

    public void success( CharSequence content )
    {
        System.err.print( "\033[1;32m" );
        print( "SUCCESS", content );
        System.err.print( "\033[1;37m" );
    }

    public void success( String name, long time )
    {
        System.err.print( "\033[1;32m" );
        print( "SUCCESS", summaryLine( name, time ) );
        System.err.print( "\033[1;37m" );
    }

    public void warning( CharSequence content )
    {
        System.err.print( "\033[1;33m" );
        print( "WARNING", content );
        System.err.print( "\033[1;37m" );
    }

    public void warning( String name, long time )
    {
        System.err.print( "\033[1;33m" );
        print( "WARNING", summaryLine( name, time ) );
        System.err.print( "\033[1;37m" );
    }

    public void failure( CharSequence content )
    {
        System.err.print( "\033[1;31m" );
        System.err.println( "[FAILURE] " + content.toString() );
        System.err.print( "\033[1;37m" );
    }

    public void failure( String name, long time )
    {
        System.err.print( "\033[1;31m" );
        System.err.println( "[FAILURE] " + summaryLine( name, time ) );
        System.err.print( "\033[1;37m" );
    }

    public void failure( Exception e )
    {
        System.err.print( "\033[1;31m" );
        System.err.println( "[FAILURE] " + e.getMessage() );
        System.err.print( "\033[1;37m" );
        if(isDebugEnabled())
           e.printStackTrace();
    }

    public void fatal( Throwable t )
    {
        System.err.print( "\033[1;31m" );
        System.err.println( "[ FATAL ] " + t.getMessage() );
        System.err.print( "\033[1;37m" );
        if(isDebugEnabled())
           t.printStackTrace();
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------
    private void print( String prefix, CharSequence content )
    {
        System.err.println( "[" + prefix + "] " + content.toString() );
    }

    private void print( String prefix, Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        System.err.println( "[" + prefix + "] " + sWriter.toString() );
    }

    private void print( String prefix, CharSequence content, Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        System.err.println( "[" + prefix + "] " + content.toString() + "\n\n" + sWriter.toString() );
    }

    private String getFormattedTime( long time )
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

    private String formatTime( long ms )
    {
        final int MS_PER_SEC  = 1000;
        final int SEC_PER_MIN = 60;

        long secs = ms / MS_PER_SEC;
        long min = secs / SEC_PER_MIN;
        secs = secs % SEC_PER_MIN;

        String msg = "";
        if ( min > 1 ) {
            msg = min + " minutes ";
        } else if ( min == 1 ) {
            msg = "1 minute ";
        }

        if ( secs > 1 ) {
            msg += secs + " seconds";
        } else if ( secs == 1 ) {
            msg += "1 second";
        } else if ( min == 0 ) {
            msg += "< 1 second";
        }
        return msg;
    }

    private String summaryLine( String name, long time )
    {
        StringBuffer messageBuffer = new StringBuffer();
        messageBuffer.append( name );

        int dotCount = 64;
        dotCount -= name.length();

        messageBuffer.append( " " );
        for ( int i = 0; i < dotCount; i++ ) {
            messageBuffer.append( '.' );
        }

        messageBuffer.append( " " );
        if ( time >= 0 ) {
            messageBuffer.append( getFormattedTime( time ) );
        } else {
            messageBuffer.append( "not built" );
        }
        return messageBuffer.toString();
    }

}
