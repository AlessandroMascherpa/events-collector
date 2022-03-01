package net.alemas.oss.tools.eventscollector.io;


/**
 * Thrown to indicate an event was not well-formed.
 *
 */
public class NotWellFormed extends IllegalArgumentException
{
    /**
     * constructs an exception without details;
     */
    public NotWellFormed()
    {
        super();
    }

    /**
     * constructs an exception with detail;
     *
     * @param message the detail message;
     */
    public NotWellFormed( String message )
    {
        super( message );
    }

    /**
     * constructs an exception with detail and cause;
     *
     * @param message the detail message;
     * @param cause   the exception cause;
     */
    public NotWellFormed( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * constructs an exception with only the cause;
     *
     * @param cause the exception cause;
     */
    public NotWellFormed( Throwable cause )
    {
        super( cause );
    }

}
