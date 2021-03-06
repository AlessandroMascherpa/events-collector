package net.alemas.oss.tools.eventscollector.io.counter;


import io.swagger.v3.oas.annotations.media.Schema;
import net.alemas.oss.tools.eventscollector.io.Base;
import net.alemas.oss.tools.eventscollector.io.NotWellFormed;


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
    private String            id;

    /* --- constructors --- */
    public Counter()
    {
        this( null, null );
    }
    public Counter
            (
                    String  system,
                    String  theId
            )
    {
        super( system );
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
    @Override
    public void isWellFormed() throws NotWellFormed
    {
        super.isWellFormed();
        if ( ( this.id == null ) || ( this.id.length() == 0 ) )
        {
            throw
                    new NotWellFormed
                            (
                                    String.format( "Class: '%s', property '%s' was not defined.", this.getClass().getSimpleName(), "id" )
                            );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        Counter that = ( o instanceof Counter ) ? ( (Counter) o ) : null;
        return
                (
                        ( that != null )
                        &&
                        super.equals( o )
                        &&
                        areEqual( that.id, that.id )
                )
                ;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + ( ( this.id != null ) ? this.id.hashCode() : 0 );
        return
                result;
    }

}
