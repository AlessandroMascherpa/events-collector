package net.alemas.oss.tools.eventscollector.io;


import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;



/**
 * Properties to define which and when an event happened;
 *
 * Created by MASCHERPA on 04/05/2021.
 */
public class CounterEvent extends Counter
{
    /* --- properties --- */
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
    protected LocalDateTime     when;

    /* --- constructors --- */
    public CounterEvent()
    {
        this( null, null );
    }
    public CounterEvent
            (
                    String id,
                    LocalDateTime when
            )
    {
        super( id );
        this.when   = when;
    }

    /* --- getters'n'setters --- */
    public LocalDateTime getWhen()
    {
        return this.when;
    }
    public void setWhen( LocalDateTime when )
    {
        this.when = when;
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
        CounterEvent that = (CounterEvent) o;
        return
                super.equals( o )
                &&
                ( ( ( this.when != null ) && ( this.when.equals( that.when ) ) ) || ( ( this.when == null ) && ( that.when == null ) ) )
                ;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + ( ( this.when != null ) ? this.when.hashCode() : 0 );
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
                +        "id: '" + this.id + "' "
                +        "at: " + Base.convertDate( this.when )
                + ']'
                ;
    }

}
