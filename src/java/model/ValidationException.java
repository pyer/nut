package nut.model;

/*
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
*/

public class ValidationException
    extends Exception
{
    /** */
    private final static String NEWLINE = System.getProperty( "line.separator" );
    
    private String expression;

    private String originalMessage;

    public ValidationException( String message )
    {
        super( message );
    }

    public ValidationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public ValidationException( String expression, String message, Throwable cause )
    {
        super( "The POM expression: " + expression + " could not be evaluated. Reason: " + message, cause );

        this.expression = expression;
        this.originalMessage = message;
    }

    public ValidationException( String expression, String message )
    {
        super( "The POM expression: " + expression + " could not be evaluated. Reason: " + message );

        this.expression = expression;
        this.originalMessage = message;
    }

    public String getExpression()
    {
        return expression;
    }

    public String getOriginalMessage()
    {
        return originalMessage;
    }

}
