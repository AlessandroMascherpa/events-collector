package net.alemas.oss.tools.eventscollector.io;


import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Common properties and methods for log in/out events and responses;
 *
 * Created by MASCHERPA on 04/02/2021.
 */
public abstract class LogInOut extends Base
{
    /* --- properties --- */
    @Schema
            (
                    required    = true,
                    description = "The username of the end user logging in or out.",
                    nullable    = false
            )
    protected String             username;

    @Schema
            (
                    required    = true,
                    description = "The application name the end user is logging in or out.",
                    nullable    = false
            )
    protected String             application;

    /* --- constructors --- */
    public LogInOut
        (
                String  name,
                String  system
        )
    {
        this.username       = name;
        this.application    = system;
    }

    /* --- getters'n'setters --- */
    public String getUsername()
    {
        return this.username;
    }
    public void setUsername( String name )
    {
        this.username = name;
    }

    public String getApplication()
    {
        return this.application;
    }
    public void setApplication( String name )
    {
        this.application = name;
    }

    /* --- object checking --- */
    public void isWellFormed() throws IllegalArgumentException
    {
        if ( ( this.username == null ) || "".equals( this.username ) )
        {
            throw
                    new IllegalArgumentException
                            (
                                    String.format( "Class: '%s', property '%s' was not defined.", this.getClass().getSimpleName(), "username" )
                            );
        }
        if ( ( this.application == null ) || "".equals( this.application ) )
        {
            throw
                    new IllegalArgumentException
                            (
                                    String.format( "Class: '%s', property '%s' was not defined.", this.getClass().getSimpleName(), "application" )
                            );
        }
    }

}
