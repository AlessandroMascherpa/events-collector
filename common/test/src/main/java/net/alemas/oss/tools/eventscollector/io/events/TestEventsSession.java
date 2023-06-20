package net.alemas.oss.tools.eventscollector.io.events;


import net.alemas.oss.tools.eventscollector.io.in.EventSession;
import net.alemas.oss.tools.eventscollector.io.out.EventsStatistics;
import net.alemas.oss.tools.eventscollector.io.payloads.EventsPayloadSession;
import net.alemas.oss.tools.eventscollector.io.payloads.PayloadSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Class with common test data for session events.
 *
 * Created by MASCHERPA on 30/10/2022.
 */
public class TestEventsSession extends TestEventsBase< EventsStatistics >
{
    /* --- checking methods --- */
    protected void checkLogInOutListResult
        (
                List< EventsStatistics >    actual,
                List< EventPayloadSession > expected
        )
    {
        assertNotNull( actual );
        assertFalse( actual.isEmpty() );
        assertEquals( expected.size(), actual.size() );
        for ( int index = 0 ; index < expected.size() ; index ++ )
        {
            EventsStatistics exp	= expected.get( index ).getResponse();
            EventsStatistics act	= actual.get( index );

            assertNotNull( "at index " + index, exp );
            assertNotNull( "at index " + index, act );

            assertEquals(  "at index " + index, exp, act );
        }
    }

    /* --- payload values --- */
    protected static final String					        USER_1		= "usr-1@system.dot";
    protected static final String					        USER_2		= "usr-2@system.dot";
    protected static final String					        USER_3		= "usr-3@system.dot";

    protected static final List< EventSession >             eventsDot;
    protected static final List< EventSession >             eventsNet;
    protected static final List< EventSession >             eventsDate;

    protected static final List< EventsStatistics >         responses;
    protected static final List< EventsStatistics >         responsesDot;

    private static final List< EventsPayloadSession >       payloadsDot  = new ArrayList<>();
    private static final List< EventsPayloadSession >       payloadsNet  = new ArrayList<>();
    private static final List< EventsPayloadSession >       payloadsDate = new ArrayList<>();

    static
    {
        payloadsDate.add
                (
                        EventsPayloadSession
                                .build( APP_DOT, USER_1 )
                                .add
                                        (
                                                PayloadSession.build
                                                        (
                                                                asDate( 2021, 2, 10, 12, 49, 3 ),
                                                                "one"
                                                        )
                                        )
                );
        payloadsDate.add
                (
                        EventsPayloadSession
                                .build( APP_DOT, USER_1 )
                                .add
                                        (
                                                PayloadSession.build
                                                        (
                                                                asDate( 2021, 2, 10, 13, 9, 0 ),
                                                                "one"
                                                        )
                                        )
                );
        payloadsDate.add
                (
                        EventsPayloadSession
                                .build( APP_DOT, USER_1 )
                                .add
                                        (
                                                PayloadSession.build
                                                        (
                                                                asDate( 2021, 2, 10, 14, 39, 3 ),
                                                                "two"
                                                        )
                                        )
                );
        payloadsDate.add
                (
                        EventsPayloadSession
                                .build( APP_DOT, USER_1 )
                                .add
                                        (
                                                PayloadSession.build
                                                        (
                                                                asDate( 2021, 2,  9, 14, 39, 3 ),
                                                                "three"
                                                        )
                                        )
                );
    }

    static
    {
        appNet.add
                (
                        new EventPayloadSession
                                (
                                        "five",
                                        new LogInOutResponse( APP_NET, USER_1, asDate( 2021, 2, 10, 13, 49, 3 ), asDate( 2021, 2, 10, 14, 19, 0 ) )
                                )
                );
        appNet.add
                (
                        new EventPayloadSession
                                (
                                        "six",
                                        new LogInOutResponse( APP_NET, USER_1, asDate( 2021, 2, 10, 14, 38, 3 ), asDate( 2021, 2, 10, 14, 45, 3 ) )
                                )
                );
        appNet.add
                (
                        new EventPayloadSession
                                (
                                        "six",
                                        new LogInOutResponse( APP_NET, USER_1, asDate( 2021, 2, 10, 14, 40, 3 ), asDate( 2021, 2, 10, 15, 15, 3 ) )
                                )
                );

        appNet.add
                (
                        new EventPayloadSession
                                (
                                        "seven",
                                        new LogInOutResponse( APP_NET, USER_1, asDate( 2021, 3, 12, 14, 38, 3 ), null )
                                )
                );
    }

    protected static final List< EventPayloadSession >	appDate     = new ArrayList<>();
    static
    {
        appDate.add
                (
                        new EventPayloadSession
                                (
                                        "ten",
                                        new LogInOutResponse( APP_ORG, USER_1, asDate( 2021, 3, 10, 12, 49, 3 ), asDate( 2021, 3, 10, 13, 9, 0 ) )
                                )
                );
        appDate.add
                (
                        new EventPayloadSession
                                (
                                        "eleven",
                                        new LogInOutResponse( APP_ORG, USER_1, asDate( 2021, 3, 10, 14, 39, 3 ), asDate( 2021, 3, 10, 14, 45, 7 ) )
                                )
                );
    }

    protected static final List< EventPayloadSession >	responses	= new ArrayList<>();
    static
    {
        responses.addAll( appDot );
        responses.addAll( appNet );
        responses.addAll( appDate );
    }

    protected final static List< EventPayloadSession >	failures	= new ArrayList<>();
    static
    {
        failures.add
                (
                        new EventPayloadSession
                                (
                                        "failure-1",
                                        new LogInOutResponse( null,    null,    null,   null )
                                )
                );
        failures.add
                (
                        new EventPayloadSession
                                (
                                        "failure-2",
                                        new LogInOutResponse( "",      null,    null,   null )
                                )
                );
        failures.add
                (
                        new EventPayloadSession
                                (
                                        "failure-3",
                                        new LogInOutResponse( "",      "",      null,   null )
                                )
                );
        failures.add
                (
                        new EventPayloadSession
                                (
                                        "failure-4",
                                        new LogInOutResponse( APP_NET, USER_2,  null,   null )
                                )
                );
        failures.add
                (
                        new EventPayloadSession
                                (
                                        "failure-5",
                                        new LogInOutResponse( APP_NET, USER_3,  null,   null )
                                )
                );
        failures.add
                (
                        new EventPayloadSession
                                (
                                        "",
                                        new LogInOutResponse( null,    null,    null,   null )
                                )
                );
        failures.add
                (
                        new EventPayloadSession
                                (
                                        null,
                                        new LogInOutResponse( null,    null,    null,   null )
                                )
                );
    }

    /* --- internal classes --- */
    protected static final class EventPayloadSession
    {
        // ?????????????????
    }

}
