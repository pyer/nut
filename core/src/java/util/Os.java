package nut.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;


public class Os
{
    // get the current info
    private static final String PATH_SEP = System.getProperty( "path.separator" );

    public static final String OS_NAME = System.getProperty( "os.name" ).toLowerCase( Locale.US );

    public static final String OS_ARCH = System.getProperty( "os.arch" ).toLowerCase( Locale.US );

    public static final String OS_VERSION = System.getProperty( "os.version" ).toLowerCase( Locale.US );

    // Make sure this method is called after static fields it depends on have been set!
    public static final String OS_FAMILY = getOsFamily();

    /**
     * Default constructor
     */
    public Os()
    {
    }

    /**
     * Determines if the current OS is one of the windows family
     */
    public static boolean isWindows( )
    {
        return OS_NAME.startsWith( "windows" );
    }

    /**
     * Helper method to determine the current OS family.
     */
    public static String getOsFamily()
    {
        if ( Os.isWindows( ) )
        {
                return "windows";
        }
        return "unix";
    }


    /**
     * Gets the shell environment variables for this process. Note that the returned mapping from variable names to
     * values will always be case-sensitive regardless of the platform, i.e. <code>getSystemEnvVars().get("path")</code>
     * and <code>getSystemEnvVars().get("PATH")</code> will in general return different values. However, on platforms
     * with case-insensitive environment variables like Windows, all variable names will be normalized to upper case.
     *
     * @return The shell environment variables, can be empty but never <code>null</code>.
     * @throws IOException If the environment variables could not be queried from the shell.
     */
    public static Properties getSystemEnvVars( )
        throws IOException
    {
        boolean caseSensitive = !isWindows();
        Process p = null;

        try
        {
            Properties envVars = new Properties();

            Runtime r = Runtime.getRuntime();

            //If this is windows set the shell to command.com or cmd.exe with correct arguments.
            if ( isWindows() )
            {
/*
                if ( Os.isFamily( Os.FAMILY_WIN9X ) )
                {
                    p = r.exec( "command.com /c set" );
                }
                else
                {
                    p = r.exec( "cmd.exe /c set" );
                }
*/
                p = r.exec( "cmd.exe /c set" );

           }
            else
            {
                p = r.exec( "env" );
            }

            BufferedReader br = new BufferedReader( new InputStreamReader( p.getInputStream() ) );

            String line;

            String lastKey = null;
            String lastVal = null;

            while ( ( line = br.readLine() ) != null )
            {
                int idx = line.indexOf( '=' );

                if ( idx > 0 )
                {
                    lastKey = line.substring( 0, idx );

                    if ( !caseSensitive )
                    {
                        lastKey = lastKey.toUpperCase( Locale.ENGLISH );
                    }

                    lastVal = line.substring( idx + 1 );

                    envVars.setProperty( lastKey, lastVal );
                }
                else if ( lastKey != null )
                {
                    lastVal += "\n" + line;

                    envVars.setProperty( lastKey, lastVal );
                }
            }

            return envVars;
        }
        finally
        {
            if ( p != null )
            {
                p.destroy();
            }
        }
    }





}
