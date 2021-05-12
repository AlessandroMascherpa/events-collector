package net.alemas.oss.tools.eventscollector;


import net.alemas.oss.tools.eventscollector.io.CounterEvent;

import java.util.ArrayList;
import java.util.List;



/**
 * Class with common test data for counter events
 *
 * Created by MASCHERPA on 11/05/2021.
 */
public class EventsCounter extends EventsBase
{
    /* --- payload values --- */
    protected static final String                   EVENT_ID_1  = "event-1";
    protected static final String                   EVENT_ID_2  = "event-2";
    protected static final String                   EVENT_ID_3  = "event-3";

    protected static final List< CounterEvent >     valid       = new ArrayList<>();
    static
    {
        valid.add( new CounterEvent( EVENT_ID_1, asDate( 2021, 5, 11, 23,  2, 12 ) ) );

        valid.add( new CounterEvent( EVENT_ID_2, asDate( 2021, 5, 11, 23,  2, 43 ) ) );
        valid.add( new CounterEvent( EVENT_ID_2, asDate( 2021, 5, 11, 23, 14, 34 ) ) );

        valid.add( new CounterEvent( EVENT_ID_3, asDate( 2021, 5, 11, 23,  2, 54 ) ) );
        valid.add( new CounterEvent( EVENT_ID_3, asDate( 2021, 5, 11, 23,  2, 55 ) ) );
        valid.add( new CounterEvent( EVENT_ID_3, asDate( 2021, 5, 11, 23,  3,  4 ) ) );
    }
}
