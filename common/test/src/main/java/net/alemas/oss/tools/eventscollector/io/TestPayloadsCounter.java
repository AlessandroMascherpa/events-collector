package net.alemas.oss.tools.eventscollector.io;


import net.alemas.oss.tools.eventscollector.io.counter.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.linking.PairApplicationIdUsernameId;

import java.time.LocalDateTime;
import java.util.*;


/**
 * Class with common test data for counter events
 *
 * Created by MASCHERPA on 11/05/2021.
 */
public class EventsCounter extends EventsBase< net.alemas.oss.tools.eventscollector.io.out.EventsCounter >
{
    /* --- payload values --- */
    protected static final String                                                       COUNT_ID_1  = "count-1";
    protected static final String                                                       COUNT_ID_2  = "count-2";
    protected static final String                                                       COUNT_ID_3  = "count-3";

    protected static final List< CounterEvent >                                         events;
    protected static final List< CounterEvent >                                         eventsDot;
    protected static final List< net.alemas.oss.tools.eventscollector.io.out.EventsCounter >                                      responses;
    protected static final List< net.alemas.oss.tools.eventscollector.io.out.EventsCounter >                                      responsesDot;
    protected static final Map< PairApplicationIdUsernameId, List< LocalDateTime > >    scenarioDot = new HashMap<>();
    protected static final Map< PairApplicationIdUsernameId, List< LocalDateTime > >    scenarioNet = new HashMap<>();
    static
    {
        scenarioDot.put
                (
                        PairApplicationIdUsernameId.build( APP_DOT, COUNT_ID_1 ),
                        Collections.singletonList
                                (
                                        asDate( 2021, 5, 11, 23, 2, 12 )
                                )
                );
        scenarioDot.put
                (
                        PairApplicationIdUsernameId.build( APP_DOT, COUNT_ID_2 ),
                        Arrays.asList
                                (
                                        asDate( 2021, 5, 11, 23,  2, 43 ),
                                        asDate( 2021, 5, 11, 23, 14, 34 )
                                )
                );
        scenarioNet.put
                (
                        PairApplicationIdUsernameId.build( APP_NET, COUNT_ID_3 ),
                        Arrays.asList
                                (
                                        asDate( 2021, 5, 11, 23, 2, 54 ),
                                        asDate( 2021, 5, 11, 23, 2, 55 ),
                                        asDate( 2021, 5, 11, 23, 3,  4 )
                                )
                );

        eventsDot       = prepareEvents( scenarioDot );
        responsesDot    = prepareResponses( scenarioDot );

        events          = prepareEvents( scenarioNet );
        responses       = prepareResponses( scenarioNet );

        events.addAll( eventsDot );
        responses.addAll( responsesDot );
    }
    private static List< CounterEvent >  prepareEvents( Map< PairApplicationIdUsernameId, List< LocalDateTime > > scenario )
    {
        List< CounterEvent >  events = new ArrayList<>();
        for ( Map.Entry< PairApplicationIdUsernameId, List< LocalDateTime > > listEntry : scenario.entrySet() )
        {
            PairApplicationIdUsernameId pair = listEntry.getKey();
            for ( LocalDateTime time : listEntry.getValue() )
            {
                events.add
                        (
                                new CounterEvent
                                        (
                                                pair.getApplication(),
                                                pair.getId(),
                                                time
                                        )
                        );
            }
        }
        return
                events;
    }
    private static List< net.alemas.oss.tools.eventscollector.io.out.EventsCounter >  prepareResponses( Map< PairApplicationIdUsernameId, List< LocalDateTime > > scenario )
    {
        List< net.alemas.oss.tools.eventscollector.io.out.EventsCounter >  responses = new ArrayList<>();
        for ( Map.Entry< PairApplicationIdUsernameId, List< LocalDateTime > > listEntry : scenario.entrySet() )
        {
            PairApplicationIdUsernameId pair = listEntry.getKey();
            responses.add
                    (
                            new net.alemas.oss.tools.eventscollector.io.out.EventsCounter
                                    (
                                            pair.getApplication(),
                                            pair.getId(),
                                            listEntry.getValue().size()
                                    )
                    );
        }
        return
                responses;
    }

    /* --- invalid payload values --- */
    protected static final List< CounterEvent >                 failures    = new ArrayList<>();
    static
    {
        failures.add( new CounterEvent( null,    "invalid", null ) );
        failures.add( new CounterEvent( null,    "",        asDate( 2021, 5, 14, 23, 24, 12 ) ) );
        failures.add( new CounterEvent( null,    null,      asDate( 2021, 5, 14, 23, 24, 36 ) ) );
        failures.add( new CounterEvent( null,    null,      null ) );

        failures.add( new CounterEvent( "",      "invalid", null ) );
        failures.add( new CounterEvent( "",      "",        asDate( 2021, 5, 14, 23, 24, 12 ) ) );
        failures.add( new CounterEvent( "",      null,      asDate( 2021, 5, 14, 23, 24, 36 ) ) );
        failures.add( new CounterEvent( "",      null,      null ) );

        failures.add( new CounterEvent( "empty", "invalid", null ) );
        failures.add( new CounterEvent( "empty", "",        asDate( 2021, 5, 14, 23, 24, 12 ) ) );
        failures.add( new CounterEvent( "empty", null,      asDate( 2021, 5, 14, 23, 24, 36 ) ) );
        failures.add( new CounterEvent( "empty", null,      null ) );

    }

}
