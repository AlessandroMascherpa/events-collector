package net.alemas.oss.tools.eventscollector.io.out;


import io.swagger.v3.oas.annotations.media.Schema;
import net.alemas.oss.tools.eventscollector.io.Base;


/**
 * Counts how many times an event occurred.
 *
 * Created by MASCHERPA on 14/10/2022.
 */
public class EventsCounter extends Base
{
    /* --- properties --- */
    @Schema
            (
                    required    = false,
                    description = "Event counter.",
                    nullable    = false
            )
    private long  counter;

    /* --- constructors --- */
    public EventsCounter()
    {
        this( null, null, 0L );
    }
    public EventsCounter
            (
                    String  application,
                    String  id,
                    long    count
            )
    {
        super( application, id );
        this.counter = count;
    }

    /* --- getters'n'setters --- */
    public long getCounter()
    {
        return this.counter;
    }
    public void setCounter( long count )
    {
        this.counter = count;
    }

    /* --- object checking --- */
    @Override
    public boolean equals( Object o )
    {
        EventsCounter that = ( o instanceof EventsCounter ) ? ( (EventsCounter) o ) : null;
        return
                (
                        ( that != null )
                        &&
                        super.equals( o )
                        &&
                        ( this.counter == that.counter )
                )
                ;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + ( (int) this.counter );
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
                                +        "counter: " + this.counter
                                + ']'
                        )
                ;
    }

}
