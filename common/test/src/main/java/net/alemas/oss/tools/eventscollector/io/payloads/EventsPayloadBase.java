package net.alemas.oss.tools.eventscollector.io.payloads;


import net.alemas.oss.tools.eventscollector.io.in.Event;
import net.alemas.oss.tools.eventscollector.io.out.EventsCounter;

import java.util.ArrayList;
import java.util.List;


public abstract class EventsPayloadBase
        <
                Payload extends PayloadBase,
                In      extends Event,
                Out     extends EventsCounter
        >
{

    /* --- properties --- */
    /**
     * the application identifier where the event occurred;
     */
    private final String            application;
    /**
     * the event identifier;
     */
    private final String            id;
    /**
     * list of events;
     */
    private final List< Payload >   payloads    = new ArrayList<>();

    /* --- constructors --- */
    protected EventsPayloadBase( String application, String id )
    {
        this.application    = application;
        this.id             = id;
    }

    public EventsPayloadBase< Payload, In, Out > add( Payload payload )
    {
        this.payloads.add( payload );
        return
                this;
    }

    /* --- getters - events --- */
    public List< In > prepareEvents()
    {
        List< In >  events  = new ArrayList<>();
        for ( Payload payload : this.payloads )
        {
            events.add
                    (
                            this.createEvent( this.application, this.id, payload )
                    );
        }
        return
                events;
    }
    protected abstract In createEvent
            (
                    String  application,
                    String  id,
                    Payload payload
            );

    /* --- getters - responses --- */
    public List< Out > prepareResponses()
    {
        return
                this.prepareResponses( this.application, this.id, this.payloads );
    }
    protected abstract List< Out > prepareResponses
            (
                    String          application,
                    String          id,
                    List< Payload > payloads
            );

}
