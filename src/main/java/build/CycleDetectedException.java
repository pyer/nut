package nut.build;

import java.util.List;

public class CycleDetectedException extends Exception
{
    private List<String> cycle;

    public CycleDetectedException( final String message, final List<String> cycle )
    {
        super( message );
        this.cycle = cycle;
    }

    public List<String> getCycle()
    {
        return cycle;
    }

    @Override
    public String getMessage()
    {
        final StringBuilder buffer = new StringBuilder();
        for ( String st : cycle ) {
            buffer.append( " --> " );
            buffer.append( st );
        }
        return super.getMessage() + buffer;
    }
}
