package net.alemas.oss.tools.eventscollector.io.events;


import net.alemas.oss.tools.eventscollector.io.in.Event;
import net.alemas.oss.tools.eventscollector.io.out.EventsCounter;
import net.alemas.oss.tools.eventscollector.io.payloads.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Class with common test data for counter events.
 * Created by MASCHERPA on 30/10/2022.
 */
public class TestEventsCounter extends TestEventsBase< EventsCounter >
{
    /* --- payload values --- */
    protected static final String                           COUNT_ID_1  = "count-1";
    protected static final String                           COUNT_ID_2  = "count-2";
    protected static final String                           COUNT_ID_3  = "count-3";

    protected static final List< Event >                    events;
    protected static final List< Event >                    eventsDot;
    protected static final List< EventsCounter >            responses;
    protected static final List< EventsCounter >            responsesDot;

    private static final List< EventsPayloadCounter >       payloadsDot = new ArrayList<>();
    private static final List< EventsPayloadCounter >       payloadsNet = new ArrayList<>();
    static
    {
        payloadsDot.add
                (
                        EventsPayloadCounter
                                .build( APP_DOT, COUNT_ID_1 )
                                .add
                                        (
                                                PayloadBase.build
                                                        (
                                                                asDate( 2021, 5, 11, 23, 2, 12 )
                                                        )
                                        )
                );
        payloadsDot.add
                (
                        EventsPayloadCounter
                                .build( APP_DOT, COUNT_ID_2 )
                                .add
                                        (
                                                PayloadBase.build
                                                        (
                                                                asDate( 2021, 5, 11, 23,  2, 43 )
                                                        )
                                        )
                                .add
                                        (
                                                PayloadBase.build
                                                        (
                                                                asDate( 2021, 5, 11, 23, 14, 34 )
                                                        )
                                        )
                );
        payloadsNet.add
                (
                        EventsPayloadCounter
                                .build( APP_NET, COUNT_ID_3 )
                                .add
                                        (
                                                PayloadBase.build
                                                        (
                                                                asDate( 2021, 5, 11, 23, 2, 54 )
                                                        )
                                        )
                                .add
                                        (
                                                PayloadBase.build
                                                        (
                                                                asDate( 2021, 5, 11, 23, 2, 55 )
                                                        )
                                        )
                                .add
                                        (
                                                PayloadBase.build
                                                        (
                                                                asDate( 2021, 5, 11, 23, 3,  4 )
                                                        )
                                        )
                );

        eventsDot       = prepareEvents( payloadsDot );
        responsesDot    = prepareResponses( payloadsDot );

        events          = prepareEvents( payloadsNet );
        responses       = prepareResponses( payloadsNet );

        events.addAll( eventsDot );
        responses.addAll( responsesDot );
    }
    private static List< Event >  prepareEvents
            (
                    List< EventsPayloadCounter > scenario
            )
    {
        return
                scenario
                        .stream()
                        .flatMap( event -> event.prepareEvents().stream() )
                        .collect( Collectors.toList() )
                ;
    }
    private static List< EventsCounter >  prepareResponses
            (
                    List< EventsPayloadCounter > scenario
            )
    {
        return
                scenario
                        .stream()
                        .flatMap( event -> event.prepareResponses().stream() )
                        .collect( Collectors.toList() )
                ;
    }

    /* --- invalid payload values --- */
    protected static final List< Event >        failures    = new ArrayList<>();
    static
    {
        failures.add( new Event( null,    "invalid", null ) );
        failures.add( new Event( null,    "",        asDate( 2021, 5, 14, 23, 24, 12 ) ) );
        failures.add( new Event( null,    null,      asDate( 2021, 5, 14, 23, 24, 36 ) ) );
        failures.add( new Event( null,    null,      null ) );

        failures.add( new Event( "",      "invalid", null ) );
        failures.add( new Event( "",      "",        asDate( 2021, 5, 14, 23, 24, 12 ) ) );
        failures.add( new Event( "",      null,      asDate( 2021, 5, 14, 23, 24, 36 ) ) );
        failures.add( new Event( "",      null,      null ) );

        failures.add( new Event( "empty", "invalid", null ) );
        failures.add( new Event( "empty", "",        asDate( 2021, 5, 14, 23, 24, 12 ) ) );
        failures.add( new Event( "empty", null,      asDate( 2021, 5, 14, 23, 24, 36 ) ) );
        failures.add( new Event( "empty", null,      null ) );
    }

}
