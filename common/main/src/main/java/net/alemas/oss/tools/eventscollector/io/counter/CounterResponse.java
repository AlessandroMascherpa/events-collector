package net.alemas.oss.tools.eventscollector.io.counter;


import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Properties to define how many times an event occurred;
 *
 * Created by MASCHERPA on 12/05/2021.
 */
public class CounterResponse extends Counter
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
    public CounterResponse()
    {
        this( null, null, 0L );
    }
    public CounterResponse
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
        CounterResponse that = (CounterResponse) o;
        return
                super.equals( o )
                &&
                ( this.counter == that.counter )
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
                this.getClass().getSimpleName()
                + '['
                +        "application: '" + this.getApplication() + "', "
                +        "id: '" + this.getId() + "' "
                +        "count: " + this.counter
                + ']'
                ;
    }

}