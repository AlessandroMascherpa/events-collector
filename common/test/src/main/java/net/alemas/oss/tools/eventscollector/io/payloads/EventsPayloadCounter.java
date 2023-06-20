package net.alemas.oss.tools.eventscollector.io.payloads;


import net.alemas.oss.tools.eventscollector.io.in.Event;
import net.alemas.oss.tools.eventscollector.io.out.EventsCounter;

import java.util.List;


public class EventsPayloadCounter extends EventsPayloadBase< PayloadBase, Event, EventsCounter >
{

    /* --- constructors --- */
    protected EventsPayloadCounter( String application, String id )
    {
        super( application, id );
    }
    public static EventsPayloadCounter build( String application, String id )
    {
        return
                new EventsPayloadCounter( application, id );
    }

    @Override
    public EventsPayloadCounter add( PayloadBase payloadBase )
    {
        return
                (EventsPayloadCounter) super.add( payloadBase );
    }

    /* --- abstract methods implementation --- */
    @Override
    protected Event createEvent
            (
                    String      application,
                    String      id,
                    PayloadBase payload
            )
    {
        return
                new Event
                        (
                                application,
                                id,
                                payload.getWhen()
                        );
    }

    @Override
    protected List< EventsCounter > prepareResponses
            (
                    String              application,
                    String              id,
                    List< PayloadBase > payloads
            )
    {
        return
                List.of
                        (
                                new EventsCounter
                                        (
                                                application,
                                                id,
                                                payloads.size()
                                        )
                        );
    }

}
