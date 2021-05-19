package net.alemas.oss.tools.eventscollector;


import net.alemas.oss.tools.eventscollector.io.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.CounterResponse;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;


/**
 * Class with common test data for counter events
 *
 * Created by MASCHERPA on 11/05/2021.
 */
public class EventsCounter extends EventsBase
{
    /* --- payload values --- */
    protected static final String                               EVENT_ID_1  = "event-1";
    protected static final String                               EVENT_ID_2  = "event-2";
    protected static final String                               EVENT_ID_3  = "event-3";

    protected static final List< CounterEvent >                 events;
    protected static final List< CounterResponse >              responses;
    protected static final Map< String, List< LocalDateTime > > scenario    = new HashMap<>();
    static
    {
        scenario.put
                (
                        EVENT_ID_1,
                        Collections.singletonList
                                (
                                        asDate( 2021, 5, 11, 23, 2, 12 )
                                )
                );
        scenario.put
                (
                        EVENT_ID_2,
                        Arrays.asList
                                (
                                        asDate( 2021, 5, 11, 23,  2, 43 ),
                                        asDate( 2021, 5, 11, 23, 14, 34 )
                                )
                );
        scenario.put
                (
                        EVENT_ID_3,
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
    private static List< CounterEvent >  prepareEvents( Map< String, List< LocalDateTime > > scenario )
    {
        List< CounterEvent >  events = new ArrayList<>();
        for ( Map.Entry< String, List< LocalDateTime > > listEntry : scenario.entrySet() )
        {
            for ( LocalDateTime time : listEntry.getValue() )
            {
                events.add
                        (
                                new CounterEvent( listEntry.getKey(), time )
                        );
            }
        }
        return
                events;
    }
    private static List< CounterResponse >  prepareResponses( Map< String, List< LocalDateTime > > scenario )
    {
        List< CounterResponse >  responses = new ArrayList<>();
        for ( Map.Entry< String, List< LocalDateTime > > listEntry : scenario.entrySet() )
        {
            responses.add
                    (
                            new CounterResponse
                                    (
                                            listEntry.getKey(),
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
        failures.add( new CounterEvent( "invalid", null ) );
        failures.add( new CounterEvent( "",        asDate( 2021, 5, 14, 23, 24, 12 ) ) );
        failures.add( new CounterEvent( null,      asDate( 2021, 5, 14, 23, 24, 36 ) ) );
        failures.add( new CounterEvent( null,      null ) );
    }


    /* --- checking methods --- */
    protected static void checkListResult
        (
                List< CounterResponse > actual,
                List< CounterResponse > expected
        )
    {
        assertNotNull( actual );
        assertFalse( actual.isEmpty() );

        assertTrue
                (
                        "Response does not contain all expected items (1) - actual: " + actual + " - expected: " + expected,
                        actual.containsAll( expected )
                );
        assertTrue
                (
                        "Response does not contain all expected items (2) - actual: " + actual + " - expected: " + expected,
                        expected.containsAll( actual )
                );
    }

}
