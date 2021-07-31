package net.alemas.oss.tools.eventscollector.io.timing;


import io.swagger.v3.oas.annotations.media.Schema;
import net.alemas.oss.tools.eventscollector.io.Base;
import net.alemas.oss.tools.eventscollector.io.counter.CounterEvent;

import java.time.LocalDateTime;



/**
 * Prooerties to defined when an event occurred and how much time it needed.
 *
 * Created by MASCHERPA on 25/05/2021.
 */
public class TimingEvent extends CounterEvent
{
    /* --- properties --- */
    @Schema
            (
                    required    = true,
                    description = "How much time the event needed.",
                    nullable    = false
            )
    private double    elapsed;

    /* --- constructors --- */
    public TimingEvent()
    {
        this( null, null, null, 0L );
    }
    public TimingEvent
            (
                    String          application,
                    String          id,
                    LocalDateTime   when,
                    double          needed
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

    public void setElapsed( double needed )
    {
        this.elapsed = needed;
    }

    /* --- object checking --- */
    @Override
    public boolean equals( Object o )
    {
        TimingEvent that = ( o instanceof TimingEvent ) ? ( (TimingEvent) o ) : null;
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
                this.getClass().getSimpleName()
                + '['
                +        "application: '" + this.getApplication() + "', "
                +        "id: '" + this.getId() + "' "
                +        "at: " + Base.convertDate( this.getWhen() ) + ", "
                +        "elapsed time: " + this.elapsed
                + ']'
                ;
    }

}
