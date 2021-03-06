package net.alemas.oss.tools.eventscollector.io.loginout;


import io.swagger.v3.oas.annotations.media.Schema;
import net.alemas.oss.tools.eventscollector.io.Base;
import net.alemas.oss.tools.eventscollector.io.NotWellFormed;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;



/**
 * The class stores a log in or out event;
 *
 * Created by MASCHERPA on 22/02/2021.
 */
public class LogInOutEvent extends LogInOut
{
    /* --- properties --- */
    @Schema
            (
                    required    = true,
                    description = "The session identifier the username is logging in or out.",
                    nullable    = false
            )
    private String              session;

    @Schema
            (
                    required    = true,
                    description = "True, if the end user is logging in; false, otherwise.",
                    nullable    = true
            )
    private boolean             in;

    @Schema
            (
                    required    = true,
                    description = "When the event occurred; the date/time pattern to use is: '" + DATE_TIME_PATTERN + "'.",
                    nullable    = true,
                    pattern     = DATE_TIME_PATTERN
            )
    @DateTimeFormat( pattern = DATE_TIME_PATTERN )
    /*
     * Note: we are using the {@code LocalDateTime} because the {@code ZonedDateTime}
     * is not correctly parsed by method {@code WebTestClient.ResponseSpec.expectBody(...)}
     * in test suite.
     */
    private LocalDateTime       when;

    /* --- constructors --- */
    public LogInOutEvent()
    {
        this( null, null, null, null, false );
    }
    public LogInOutEvent
        (
                String          system,
                String          name,
                String          idSession,
                LocalDateTime   date,
                boolean         enter
        )
    {
        super( system, name );
        this.session    = idSession;
        this.in         = enter;
        this.when       = date;
    }

    /* --- getters'n'setters --- */
    public String getSession()
    {
        return
                this.session;
    }
    public void setSession( String id )
    {
        this.session = id;
    }

    public boolean isIn()
    {
        return this.in;
    }
    public void setIn( boolean in )
    {
        this.in = in;
    }

    public LocalDateTime getWhen()
    {
        return this.when;
    }
    public void setWhen( LocalDateTime date )
    {
        this.when = date;
    }

    /* --- object checking --- */
    @Override
    public void isWellFormed() throws NotWellFormed
    {
        super.isWellFormed();
        if ( ( this.session == null ) || "".equals( this.session ) )
        {
            throw
                    new NotWellFormed
                            (
                                    String.format( "Class: '%s', property '%s' was not defined.", this.getClass().getSimpleName(), "session" )
                            );
        }
        if ( this.when == null )
        {
            throw
                    new NotWellFormed
                            (
                                    String.format( "Class: '%s', property '%s' was not defined.", this.getClass().getSimpleName(), "when" )
                            );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        LogInOutEvent that = ( o instanceof  LogInOutEvent ) ? ( (LogInOutEvent) o ) : null;
        return
                (
                        ( that != null )
                        &&
                        super.equals( o )
                        &&
                        areEqual( this.session, that.session )
                        &&
                        ( this.in == that.in )
                        &&
                        areEqual( this.when, that.when )
                )
                ;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + ( ( this.session != null ) ? this.session.hashCode() : 0 );
        result = 31 * result + ( ( this.when != null )    ? this.when.hashCode() : 0 );
        result = 31 * result + ( this.in ? 1 : 0 );
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
                                +        "user: '" + this.getUsername() + "' "
                                +        "session id: '" + this.getSession() + "' "
                                +        "logged " + ( this.in ? "in to" : "out from" ) + ' '
                                +        "application: '" + this.getApplication() + "' "
                                +        "at: " + Base.convertDate( this.when )
                                + ']'
                        )
                ;
    }

}
