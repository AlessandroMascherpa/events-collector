package net.alemas.oss.tools.eventscollector.io;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;


/**
 * Collection of end user activity, when the end user logged in and out
 * and at which application;
 *
 * Created by MASCHERPA on 05/02/2021.
 */
public class LogInOutResponse extends LogInOut
{
    /* --- properties --- */
    /*
     * Note: we are using the {@code LocalDateTime} because the {@code ZonedDateTime}
     * is not correctly parsed by method {@code WebTestClient.ResponseSpec.expectBody(...)}
     * in test suite.
     */
    @Schema
            (
                    required    = true,
                    description = "Date and time when the end user logged in; the date/time pattern is: '" + DATE_TIME_PATTERN + "'.",
                    nullable    = true,
                    pattern     = DATE_TIME_PATTERN
            )
    private final LocalDateTime dateLoggedIn;

    /*
     * Note: we are using the {@code LocalDateTime} because the {@code ZonedDateTime}
     * is not correctly parsed by method {@code WebTestClient.ResponseSpec.expectBody(...)}
     * in test suite.
     */
    @Schema
            (
                    required    = true,
                    description = "Date and time when the end user logged out; the date/time pattern is: '" + DATE_TIME_PATTERN + "'.",
                    nullable    = true,
                    pattern     = DATE_TIME_PATTERN
            )
    private final LocalDateTime dateLoggedOut;

    /* --- constructors --- */
    public LogInOutResponse
        (
                @JsonProperty( "username" )         String          name,
                @JsonProperty( "application" )      String          system,
                @JsonProperty( "dateLoggedIn" )     LocalDateTime   dateIn,
                @JsonProperty( "dateLoggedOut" )    LocalDateTime   dateOut
        )
    {
        super( name, system );
        this.dateLoggedIn   = dateIn;
        this.dateLoggedOut  = dateOut;
    }

    /* --- getters --- */
    public LocalDateTime getDateLoggedIn()
    {
        return this.dateLoggedIn;
    }

    public LocalDateTime getDateLoggedOut()
    {
        return this.dateLoggedOut;
    }

    /* --- object checking --- */
    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( ( o == null ) || ( this.getClass() != o.getClass() ) )  return false;

        LogInOutResponse that = (LogInOutResponse) o;
        if ( ( this.username != null )     ? ( ! this.username.equals( that.username ) )         : ( that.username != null ) )      return false;
        if ( ( this.application != null )  ? ( ! this.application.equals( that.application ) )   : ( that.application != null ) )   return false;
        if ( ( this.dateLoggedIn != null ) ? ( ! this.dateLoggedIn.equals( that.dateLoggedIn ) ) : ( that.dateLoggedIn != null ) )  return false;

        return
                !( ( this.dateLoggedOut != null ) ? ( ! this.dateLoggedOut.equals( that.dateLoggedOut ) ) : ( that.dateLoggedOut != null ) );
    }

    @Override
    public int hashCode()
    {
        int result = ( this.username != null ) ? this.username.hashCode() : 0;
        result = 31 * result + ( ( this.application   != null ) ? this.application.hashCode() : 0 );
        result = 31 * result + ( ( this.dateLoggedIn  != null ) ? this.dateLoggedIn.hashCode() : 0 );
        result = 31 * result + ( ( this.dateLoggedOut != null ) ? this.dateLoggedOut.hashCode() : 0 );
        return
                result;
    }

    /* --- logging --- */
    @Override
    public String toString()
    {
        return
                this.getClass().getSimpleName()
                        + '['
                        +        "user: '" + this.username + "', "
                        +        "application: '" + this.application + "', "
                        +        "logged in at: " + Base.convertDate( this.dateLoggedIn ) + "', "
                        +        "logged out at: " + Base.convertDate( this.dateLoggedOut )
                        + ']'
                ;
    }

}
