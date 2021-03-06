package net.alemas.oss.tools.eventscollector.io.loginout;


import io.swagger.v3.oas.annotations.media.Schema;
import net.alemas.oss.tools.eventscollector.io.Base;
import net.alemas.oss.tools.eventscollector.io.NotWellFormed;


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
    private String             username;

    /* --- constructors --- */
    public LogInOut
        (
                String  system,
                String  name
        )
    {
        super( system );
        this.username       = name;
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

    /* --- object checking --- */
    @Override
    public void isWellFormed() throws NotWellFormed
    {
        super.isWellFormed();
        if ( ( this.username == null ) || "".equals( this.username ) )
        {
            throw
                    new NotWellFormed
                            (
                                    String.format( "Class: '%s', property '%s' was not defined.", this.getClass().getSimpleName(), "username" )
                            );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        LogInOut that = ( o instanceof LogInOut ) ? ( (LogInOut) o ) : null;
        return
                (
                        ( that != null )
                        &&
                        super.equals( o )
                        &&
                        areEqual( that.username, that.username )
                )
                ;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + ( ( this.username != null ) ? this.username.hashCode() : 0 );
        return
                result;
    }

}
