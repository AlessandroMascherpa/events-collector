package net.alemas.oss.tools.eventscollector.io.out;


import io.swagger.v3.oas.annotations.media.Schema;
import net.alemas.oss.tools.eventscollector.io.Base;


/**
 * Properties to define how many times an event occurred and how much time it needed.
 *
 * Created by MASCHERPA on 02/11/2022.
 */
public class EventsStatistics extends EventsCounter
{
    /* --- properties --- */
    @Schema
            (
                    required    = false,
                    description = "Average time the event needed.",
                    nullable    = false
            )
    private double    average;
    
    @Schema
            (
                    required    = false,
                    description = "Minimum time the event needed.",
                    nullable    = false
            )
    private double    min;
    
    @Schema
            (
                    required    = false,
                    description = "Maximum time the event needed.",
                    nullable    = false
            )
    private double    max;
            
    /* --- constructors --- */
    public EventsStatistics()
    {
     this( null, null, 0L, 0.0D, 0.0D, 0.0D );
    }
    public EventsStatistics
            (
                    String  application,
                    String  id,
                    long    count,
                    double  avg,
                    double  min,
                    double  max
            )
    {
        super( application, id, count );
        this.average    = avg;
        this.min        = min;
        this.max        = max;
    }

    /* --- getters'n'setters --- */
    public double getAverage()
    {
        return this.average;
    }
    public void setAverage( double avg )
    {
        this.average = avg;
    }

    public double getMin()
    {
        return this.min;
    }
    public void setMin( double min )
    {
        this.min = min;
    }

    public double getMax()
    {
        return this.max;
    }
    public void setMax( double max )
    {
        this.max = max;
    }

    /* --- object checking --- */
    @Override
    public boolean equals( Object o )
    {
        EventsStatistics that = ( o instanceof EventsStatistics ) ? ( (EventsStatistics) o ) : null;
        return
                (
                        ( that != null )
                        &&
                        super.equals( o )
                        &&
                        ( this.average == that.average )
                        &&
                        ( this.min == that.min )
                        &&
                        ( this.max == that.max )
                )
                ;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + Double.hashCode( this.average );
        result = 31 * result + Double.hashCode( this.min );
        result = 31 * result + Double.hashCode( this.max );
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
                                +        "counter: " + this.getCounter() + ", "
                                +        "average time: " + this.average + ", "
                                +        "min time: " + this.min + ", "
                                +        "max time: " + this.max
                                + ']'
                        )
                ;
    }

}
