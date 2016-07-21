package nut.goals;

public class GoalException
    extends Exception
{
    public GoalException( String message )
    {
        super( message );
    }

    public GoalException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
