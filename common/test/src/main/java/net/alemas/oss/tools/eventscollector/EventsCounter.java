package net.alemas.oss.tools.eventscollector;


import net.alemas.oss.tools.eventscollector.io.counter.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.counter.CounterResponse;
import net.alemas.oss.tools.eventscollector.io.linking.PairApplicationIdUsernameId;

import java.time.LocalDateTime;
import java.util.*;


/**
 * Class with common test data for counter events
 *
 * Created by MASCHERPA on 11/05/2021.
 */
public class EventsCounter extends EventsBase< CounterResponse >
{
    /* --- payload values --- */
    protected static final String                                                       COUNT_ID_1  = "count-1";
    protected static final String                                                       COUNT_ID_2  = "count-2";
    protected static final String                                                       COUNT_ID_3  = "count-3";

    protected static final List< CounterEvent >                                         events;
    protected static final List< CounterResponse >                                      responses;
    protected static final Map< PairApplicationIdUsernameId, List< LocalDateTime > >    scenario    = new HashMap<>();
    static
    {
        scenario.put
                (
                        PairApplicationIdUsernameId.build( APP_DOT, COUNT_ID_1 ),
                        Collections.singletonList
                                (
                                        asDate( 2021, 5, 11, 23, 2, 12 )
                                )
                );
        scenario.put
                (
                        PairApplicationIdUsernameId.build( APP_DOT, COUNT_ID_2 ),
                        Arrays.asList
                                (
                                        asDate( 2021, 5, 11, 23,  2, 43 ),
                                        asDate( 2021, 5, 11, 23, 14, 34 )
                                )
                );
        scenario.put
                (
                        PairApplicationIdUsernameId.build( APP_NET, COUNT_ID_3 ),
                        Arrays.asList
                                (
                                        asDate( 2021, 5, 11, 23, 2, 54 ),
                                        asDate( 2021, 5, 11, 23, 2, 55 ),
                                        asDate( 2021, 5, 11, 23, 3,  4 )
                                )
                );

        events      = prepareEvents( scenario );
        responses   = prepareResponses( scenario );
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
    private static List< CounterResponse >  prepareResponses( Map< PairApplicationIdUsernameId, List< LocalDateTime > > scenario )
    {
        List< CounterResponse >  responses = new ArrayList<>();
        for ( Map.Entry< PairApplicationIdUsernameId, List< LocalDateTime > > listEntry : scenario.entrySet() )
        {
            PairApplicationIdUsernameId pair = listEntry.getKey();
            responses.add
                    (
                            new CounterResponse
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
