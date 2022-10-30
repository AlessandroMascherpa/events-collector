package net.alemas.oss.tools.eventscollector.io.in;


import io.swagger.v3.oas.annotations.media.Schema;
import net.alemas.oss.tools.eventscollector.io.Base;
import net.alemas.oss.tools.eventscollector.io.NotWellFormed;

import java.time.LocalDateTime;


/**
 * An event belonging to session occurred.
 *
 * Created by MASCHERPA on 14/10/2022.
 */
public class EventSession extends Event
{
    /* --- properties --- */
    @Schema
            (
                    required    = true,
                    description = "The session identifier where the event occurred.",
                    nullable    = false
            )
    private String  session;

    /* --- constructors --- */
    public EventSession()
    {
        this( null, null, null, null );
    }
    public EventSession
            (
                    String          application,
                    String          id,
                    LocalDateTime   when,
                    String          idSession
            )
    {
        super( application, id, when );
        this.session = idSession;
    }

    /* --- getters'n'setters --- */
    public String getSession()
    {
        return this.session;
    }

    public void setSession( String id )
    {
        this.session = id;
    }

    /* --- object checking --- */
    @Override
    public void isWellFormed() throws NotWellFormed
    {
        super.isWellFormed();
        if ( this.session == null )
        {
            throw
                    new NotWellFormed
                            (
                                    this.getClass().getSimpleName(),
                                    "session"
                            );
        }
    }

    /* --- object checking --- */
    @Override
    public boolean equals( Object o )
    {
        EventSession that = ( o instanceof EventSession ) ? ( (EventSession) o ) : null;
        return
                (
                        ( that != null )
                        &&
                        super.equals( o )
                        &&
                        areEqual( this.session, that.session )
                )
                ;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + ( ( this.session != null ) ? this.session.hashCode() : 0 );
        return
                result;
    }

    /* --- debugging --- */
    @Override
    public String toString()
    {
        return
                Base.safeLogString
                        (
                                this.getClass().getSimpleName()
                                + '['
                                +        "application: '" + this.getApplication() + "', "
                                +        "id: '" + this.getId() + "', "
                                +        "at: " + Base.convertDate( this.getWhen() ) + ", "
                                +        "session id: '" + this.session +'\''
                                + ']'
                        )
                ;
    }

}
