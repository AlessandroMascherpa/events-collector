package net.alemas.oss.tools.eventscollector.io.events;


import net.alemas.oss.tools.eventscollector.io.in.EventElapsed;
import net.alemas.oss.tools.eventscollector.io.out.EventsStatistics;
import net.alemas.oss.tools.eventscollector.io.payloads.EventsPayloadElapsed;
import net.alemas.oss.tools.eventscollector.io.payloads.PayloadElapsed;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Class with common test data for timer events.
 * Created by MASCHERPA on 30/10/2022.
 */
public class TestEventsElapsed extends TestEventsBase< EventsStatistics >
{
    /* --- payload values --- */
    protected static final String                           TIMER_ID_1 = "timer-1";
    protected static final String                           TIMER_ID_2 = "timer-2";
    protected static final String                           TIMER_ID_3 = "timer-3";

    protected static final List< EventElapsed >             events;
    protected static final List< EventElapsed >             eventsDot;
    protected static final List< EventsStatistics >         responses;
    protected static final List< EventsStatistics >         responsesDot;

    private static final List< EventsPayloadElapsed >       payloadsDot = new ArrayList<>();
    private static final List< EventsPayloadElapsed >       payloadsOrg = new ArrayList<>();

    static
    {
        payloadsDot.add
                (
                        EventsPayloadElapsed
                                .build( APP_DOT, TIMER_ID_1 )
                                .add
                                        (
                                                PayloadElapsed.build
                                                        (
                                                                asDate( 2021, 5, 21, 22, 5, 3 ),
                                                                10L
                                                        )
                                        )
                );
        payloadsDot.add
                (
                        EventsPayloadElapsed
                                .build( APP_DOT, TIMER_ID_2 )
                                .add
                                        (
                                                PayloadElapsed.build
                                                        (
                                                                asDate( 2021, 5, 21, 22, 15, 2 ),
                                                                20L
                                                        )
                                        )
                                .add
                                        (
                                                PayloadElapsed.build
                                                        (
                                                                asDate( 2021, 5, 21, 22, 55, 22 ),
                                                                40L
                                                        )
                                        )
                );
        payloadsOrg.add
                (
                        EventsPayloadElapsed
                                .build( APP_ORG, TIMER_ID_3 )
                                .add
                                        (
                                                PayloadElapsed.build
                                                        (
                                                                asDate( 2021, 5, 22, 21, 43, 45 ),
                                                                10L
                                                        )
                                        )
                                .add
                                        (
                                                PayloadElapsed.build
                                                        (
                                                                asDate( 2021, 5, 22, 21, 52, 36 ),
                                                                20L
                                                        )
                                        )
                                .add
                                        (
                                                PayloadElapsed.build
                                                        (
                                                                asDate( 2021, 5, 22, 21, 2, 12 ),
                                                                30L
                                                        )
                                        )
                );

        eventsDot       = prepareEvents( payloadsDot );
        responsesDot    = prepareResponses( payloadsDot );

        events          = prepareEvents( payloadsOrg );
        responses       = prepareResponses( payloadsOrg );

        events.addAll( eventsDot );
        responses.addAll( responsesDot );
    }
    private static List< EventElapsed >  prepareEvents
            (
                    List< EventsPayloadElapsed > scenario
            )
    {
        return
                scenario
                        .stream()
                        .flatMap( event -> event.prepareEvents().stream() )
                        .collect( Collectors.toList() )
                ;
    }
    private static List< EventsStatistics >  prepareResponses
            (
                    List< EventsPayloadElapsed > scenario
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
    protected static final List< EventElapsed >                 failures    = new ArrayList<>();
    static
    {
        failures.add( new EventElapsed( null,    "invalid", null,                              0L ) );
        failures.add( new EventElapsed( null,    "",        asDate( 2021, 5, 14, 23, 24, 12 ), 0L ) );
        failures.add( new EventElapsed( null,    null,      asDate( 2021, 5, 14, 23, 24, 36 ), 0L ) );
        failures.add( new EventElapsed( null,    null,      null,                              0L ) );

        failures.add( new EventElapsed( "",      "invalid", null,                              0L ) );
        failures.add( new EventElapsed( "",      "",        asDate( 2021, 5, 14, 23, 24, 12 ), 0L ) );
        failures.add( new EventElapsed( "",      null,      asDate( 2021, 5, 14, 23, 24, 36 ), 0L ) );
        failures.add( new EventElapsed( "",      null,      null,                              0L ) );

        failures.add( new EventElapsed( "empty", "invalid", null,                              0L ) );
        failures.add( new EventElapsed( "empty", "",        asDate( 2021, 5, 14, 23, 24, 12 ), 0L ) );
        failures.add( new EventElapsed( "empty", null,      asDate( 2021, 5, 14, 23, 24, 36 ), 0L ) );
        failures.add( new EventElapsed( "empty", null,      null,                              0L ) );
    }

}
