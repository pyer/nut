package nut.project.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelValidationException
    extends Exception
{
    /** */
    private final static String NEWLINE = System.getProperty( "line.separator" );
    
    private String expression;

    private String originalMessage;

    public ModelValidationException( String message )
    {
        super( message );
    }

    public ModelValidationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public ModelValidationException( String expression, String message, Throwable cause )
    {
        super( "The POM expression: " + expression + " could not be evaluated. Reason: " + message, cause );

        this.expression = expression;
        this.originalMessage = message;
    }

    public ModelValidationException( String expression, String message )
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

   
    
    
    
    /*
    private List messages;

    public ModelValidationResult()
    {
        messages = new ArrayList();
    }

    public int getMessageCount()
    {
        return messages.size();
    }

    public String getMessage( int i )
    {
        return messages.get( i ).toString();
    }

    public List getMessages()
    {
        return Collections.unmodifiableList( messages );
    }

    public void addMessage( String message )
    {
        messages.add( message );
    }

    public String toString()
    {
        return render( "" );
    }

    public String render( String indentation )
    {
        if ( messages.size() == 0 )
        {
            return indentation + "There were no validation errors.";
        }

        StringBuffer message = new StringBuffer();

//        if ( messages.size() == 1 )
//        {
//            message.append( "There was 1 validation error: " );
//        }
//        else
//        {
//            message.append( "There was " + messages.size() + " validation errors: " + NEWLINE );
//        }
//
        for ( int i = 0; i < messages.size(); i++ )
        {
            message.append( indentation + "[" + i + "]  " + messages.get( i ).toString() + NEWLINE );
        }

        return message.toString();
    }
*/
}
