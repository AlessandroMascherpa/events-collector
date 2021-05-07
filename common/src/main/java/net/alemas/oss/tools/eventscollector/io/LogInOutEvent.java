package net.alemas.oss.tools.eventscollector.io;


import io.swagger.v3.oas.annotations.media.Schema;
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
        this( null, null, null, false );
    }
    public LogInOutEvent
        (
                String          name,
                String          system,
                LocalDateTime   date,
                boolean         enter
        )
    {
        super( name, system );
        this.in     = enter;
        this.when   = date;
    }

    /* --- getters'n'setters --- */
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
    public void isWellFormed() throws IllegalArgumentException
    {
        super.isWellFormed();
        if ( this.when == null )
        {
            throw
                    new IllegalArgumentException
                            (
                                    String.format( "Class: '%s', property '%s' was not defined.", this.getClass().getSimpleName(), "when" )
                            );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )  return true;
        if ( ( o == null ) || ( this.getClass() != o.getClass() ) )  return false;

        LogInOutEvent that = (LogInOutEvent) o;
        return
                ( this.in == that.in )
                &&
                ( ( ( this.username != null ) && ( this.username.equals( that.username ) ) ) || ( ( this.username == null ) && ( that.username == null ) ) )
                &&
                ( ( ( this.application != null ) && ( this.application.equals( that.application ) ) ) || ( ( this.application == null ) && ( that.application == null ) ) )
                &&
                ( ( ( this.when != null ) && ( this.when.equals( that.when ) ) ) || ( ( this.when == null ) && ( that.when == null ) ) )
                ;
    }

    @Override
    public int hashCode()
    {
        int result = ( this.username != null ) ? this.username.hashCode() : 0;
        result = 31 * result + ( ( this.application   != null ) ? this.application.hashCode() : 0 );
        result = 31 * result + ( ( this.when          != null ) ? this.when.hashCode() : 0 );
        result = 31 * result + ( this.in ? 1 : 0 );
        return
                result;
    }

    /* --- debugging --- */
    @Override
    public String toString()
    {
        return
                this.getClass().getSimpleName()
                + '['
                +        "user: '" + this.username + "' "
                +        "logged " + ( this.in ? "in to" : "out from" ) + ' '
                +        "application: '" + this.application + "' "
                +        "at: " + LogInOut.convertDate( this.when )
                + ']'
                ;
    }

}
