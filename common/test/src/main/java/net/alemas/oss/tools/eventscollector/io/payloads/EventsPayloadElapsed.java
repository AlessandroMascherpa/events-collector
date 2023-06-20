package net.alemas.oss.tools.eventscollector.io.payloads;


import net.alemas.oss.tools.eventscollector.io.in.EventElapsed;
import net.alemas.oss.tools.eventscollector.io.out.EventsStatistics;

import java.util.DoubleSummaryStatistics;
import java.util.List;


public class EventsPayloadElapsed extends EventsPayloadBase< PayloadElapsed, EventElapsed, EventsStatistics >
{

    /* --- constructors --- */
    protected EventsPayloadElapsed( String application, String id )
    {
        super( application, id );
    }
    public static EventsPayloadElapsed build( String application, String id )
    {
        return
                new EventsPayloadElapsed( application, id );
    }

    @Override
    public EventsPayloadElapsed add( PayloadElapsed payloadElapsed )
    {
        return
                (EventsPayloadElapsed) super.add( payloadElapsed );
    }

    /* --- abstract methods implementation --- */
    @Override
    protected EventElapsed createEvent
            (
                    String          application,
                    String          id,
                    PayloadElapsed  payload
            )
    {
        return
                new EventElapsed
                        (
                                application,
                                id,
                                payload.getWhen(),
                                payload.getElapsed()
                        );
    }

    @Override
    protected List< EventsStatistics > prepareResponses
            (
                    String                  application,
                    String                  id,
                    List< PayloadElapsed >  payloads
            )
    {
        DoubleSummaryStatistics stats   =
                payloads
                        .stream()
                        .mapToDouble( value -> value.getElapsed() )
                        .summaryStatistics()
                ;
        return
                List.of
                        (
                                new EventsStatistics
                                        (
                                                application,
                                                id,
                                                stats.getCount(),
                                                stats.getAverage(),
                                                stats.getMin(),
                                                stats.getMax()
                                        )
                        )
                ;
    }
    
}
