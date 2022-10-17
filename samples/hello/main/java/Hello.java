
public class Hello
{
    public static void main( String[] args )
    {
        String world = "world";
        if ( args.length > 0 ) {
          world = args[0];
        }
        System.out.println( "Hello " + world );
        System.exit( 0 );
    }
}
