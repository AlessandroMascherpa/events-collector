package net.alemas.oss.tools.eventscollector.io.loginout;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import net.alemas.oss.tools.eventscollector.io.Base;

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
                @JsonProperty( "application" )      String          system,
                @JsonProperty( "username" )         String          name,
                @JsonProperty( "dateLoggedIn" )     LocalDateTime   dateIn,
                @JsonProperty( "dateLoggedOut" )    LocalDateTime   dateOut
        )
    {
        super( system, name );
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
        LogInOutResponse that = (LogInOutResponse) o;
        return
                super.equals( o )
                &&
                ( ( ( this.dateLoggedIn != null ) && ( this.dateLoggedIn.equals( that.dateLoggedIn ) ) ) || ( ( this.dateLoggedIn == null ) && ( that.dateLoggedIn == null ) ) )
                &&
                ( ( ( this.dateLoggedOut != null ) && ( this.dateLoggedOut.equals( that.dateLoggedOut ) ) ) || ( ( this.dateLoggedOut == null ) && ( that.dateLoggedOut == null ) ) )
                ;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
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
                        +        "application: '" + this.getApplication() + "', "
                        +        "user: '" + this.getUsername() + "', "
                        +        "logged in at: " + Base.convertDate( this.dateLoggedIn ) + "', "
                        +        "logged out at: " + convertDate( this.dateLoggedOut )
                        + ']'
                ;
    }

}
