package net.alemas.oss.tools.eventscollector.io.in;


import io.swagger.v3.oas.annotations.media.Schema;
import net.alemas.oss.tools.eventscollector.io.Base;

import java.time.LocalDateTime;



/**
 * Elapsed time for an event.
 * Created by MASCHERPA on 14/10/2022.
 */
public class EventElapsed extends Event
{
    /* --- properties --- */
    @Schema
            (
                    required    = true,
                    description = "How much time the event needed.",
                    nullable    = false
            )
    private long        elapsed;

    /* --- constructors --- */
    public EventElapsed()
    {
        this( null, null, null, 0L );
    }
    public EventElapsed
            (
                    String          application,
                    String          id,
                    LocalDateTime   when,
                    long            needed
            )
    {
        super( application, id, when );
        this.elapsed = needed;
    }

    /* --- getters'n'setters --- */
    public double getElapsed()
    {
        return this.elapsed;
    }

    public void setElapsed( long needed )
    {
        this.elapsed = needed;
    }

    /* --- object checking --- */
    @Override
    public boolean equals( Object o )
    {
        EventElapsed that = ( o instanceof EventElapsed ) ? ( (EventElapsed) o ) : null;
        return
                (
                        ( that != null )
                        &&
                        super.equals( o )
                        &&
                        ( this.elapsed == that.elapsed )
                )
                ;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + Double.hashCode( this.elapsed );
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
                                +        "at: " + Base.convertDate( this.getWhen() ) + ", "
                                +        "elapsed time: " + this.elapsed
                                + ']'
                        )
                ;
    }

}
