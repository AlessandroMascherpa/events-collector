package net.alemas.oss.tools.eventscollector.io;


import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Base properties for events;
 *
 * Created by MASCHERPA on 12/05/2021.
 */
public class Counter extends Base
{
    /* --- properties --- */
    @Schema
            (
                    required    = true,
                    description = "The unique event identifier.",
                    nullable    = false
            )
    protected String            id;

    /* --- constructors --- */
    public Counter()
    {
        this( null );
    }
    public Counter( String theId )
    {
        this.id = theId;
    }

    /* --- getters'n'setters --- */
    public String getId()
    {
        return this.id;
    }
    public void setId( String id )
    {
        this.id = id;
    }

    /* --- object checking --- */
    public void isWellFormed() throws IllegalArgumentException
    {
        if ( ( this.id == null ) || ( this.id.length() == 0 ) )
        {
            throw
                    new IllegalArgumentException
                            (
                                    String.format( "Class: '%s', property '%s' was not defined.", this.getClass().getSimpleName(), "id" )
                            );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )  return true;
        if ( ( o == null ) || ( this.getClass() != o.getClass() ) )  return false;

        Counter that = (Counter) o;
        return
                ( ( ( this.id != null ) && ( this.id.equals( that.id ) ) ) || ( ( this.id == null ) && ( that.id == null ) ) )
                ;
    }

    @Override
    public int hashCode()
    {
        return
                ( this.id != null ) ? this.id.hashCode() : 0
                ;
    }

}
