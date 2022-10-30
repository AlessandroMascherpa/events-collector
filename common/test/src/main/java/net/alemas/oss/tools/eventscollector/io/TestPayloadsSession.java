package net.alemas.oss.tools.eventscollector.io;


import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Class with common test data for log in/out events.
 *
 * Created by MASCHERPA on 26/02/2021.
 */
public class EventsLogInOut extends TestPayloadsBase< LogInOutResponse >
{
    /* --- checking methods --- */
    protected void checkLogInOutListResult
        (
                List< LogInOutResponse >    actual,
                List< LogInOutSessionTest > expected
        )
    {
        assertNotNull( actual );
        assertFalse( actual.isEmpty() );
        assertEquals( expected.size(), actual.size() );
        for ( int index = 0 ; index < expected.size() ; index ++ )
        {
            LogInOutResponse exp	= expected.get( index ).getResponse();
            LogInOutResponse act	= actual.get( index );

            assertNotNull( "at index " + index, exp );
            assertNotNull( "at index " + index, act );

            assertEquals(  "at index " + index, exp, act );
        }
    }

    /* --- payload values --- */
    protected static final String					USER_1		= "usr-1@system.dot";
    protected static final String					USER_2		= "usr-2@system.dot";
    protected static final String					USER_3		= "usr-3@system.dot";

    protected static final List< LogInOutSessionTest > appDot      = new ArrayList<>();
    static
    {
        appDot.add
                (
                        new LogInOutSessionTest
                                (
                                        "one",
                                        new LogInOutResponse( APP_DOT, USER_1, asDate( 2021, 2, 10, 12, 49, 3 ), asDate( 2021, 2, 10, 13, 9, 0 ) )
                                )
                );
        appDot.add
                (
                        new LogInOutSessionTest
                                (
                                        "two",
                                        new LogInOutResponse( APP_DOT, USER_1, asDate( 2021, 2, 10, 14, 39, 3 ), null )
                                )
                );

        appDot.add
                (
                        new LogInOutSessionTest
                                (
                                        "three",
                                        new LogInOutResponse( APP_DOT, USER_2, asDate( 2021, 2,  9, 14, 39, 3 ), null )
                                )
                );
    }

    protected static final List< LogInOutSessionTest >	appNet      = new ArrayList<>();
    static
    {
        appNet.add
                (
                        new LogInOutSessionTest
                                (
                                        "five",
                                        new LogInOutResponse( APP_NET, USER_1, asDate( 2021, 2, 10, 13, 49, 3 ), asDate( 2021, 2, 10, 14, 19, 0 ) )
                                )
                );
        appNet.add
                (
                        new LogInOutSessionTest
                                (
                                        "six",
                                        new LogInOutResponse( APP_NET, USER_1, asDate( 2021, 2, 10, 14, 38, 3 ), asDate( 2021, 2, 10, 14, 45, 3 ) )
                                )
                );
        appNet.add
                (
                        new LogInOutSessionTest
                                (
                                        "six",
                                        new LogInOutResponse( APP_NET, USER_1, asDate( 2021, 2, 10, 14, 40, 3 ), asDate( 2021, 2, 10, 15, 15, 3 ) )
                                )
                );

        appNet.add
                (
                        new LogInOutSessionTest
                                (
                                        "seven",
                                        new LogInOutResponse( APP_NET, USER_1, asDate( 2021, 3, 12, 14, 38, 3 ), null )
                                )
                );
    }

    protected static final List< LogInOutSessionTest >	appDate     = new ArrayList<>();
    static
    {
        appDate.add
                (
                        new LogInOutSessionTest
                                (
                                        "ten",
                                        new LogInOutResponse( APP_ORG, USER_1, asDate( 2021, 3, 10, 12, 49, 3 ), asDate( 2021, 3, 10, 13, 9, 0 ) )
                                )
                );
        appDate.add
                (
                        new LogInOutSessionTest
                                (
                                        "eleven",
                                        new LogInOutResponse( APP_ORG, USER_1, asDate( 2021, 3, 10, 14, 39, 3 ), asDate( 2021, 3, 10, 14, 45, 7 ) )
                                )
                );
    }

    protected static final List< LogInOutSessionTest >	responses	= new ArrayList<>();
    static
    {
        responses.addAll( appDot );
        responses.addAll( appNet );
        responses.addAll( appDate );
    }

    protected final static List< LogInOutSessionTest >	failures	= new ArrayList<>();
    static
    {
        failures.add
                (
                        new LogInOutSessionTest
                                (
                                        "failure-1",
                                        new LogInOutResponse( null,    null,    null,   null )
                                )
                );
        failures.add
                (
                        new LogInOutSessionTest
                                (
                                        "failure-2",
                                        new LogInOutResponse( "",      null,    null,   null )
                                )
                );
        failures.add
                (
                        new LogInOutSessionTest
                                (
                                        "failure-3",
                                        new LogInOutResponse( "",      "",      null,   null )
                                )
                );
        failures.add
                (
                        new LogInOutSessionTest
                                (
                                        "failure-4",
                                        new LogInOutResponse( APP_NET, USER_2,  null,   null )
                                )
                );
        failures.add
                (
                        new LogInOutSessionTest
                                (
                                        "failure-5",
                                        new LogInOutResponse( APP_NET, USER_3,  null,   null )
                                )
                );
        failures.add
                (
                        new LogInOutSessionTest
                                (
                                        "",
                                        new LogInOutResponse( null,    null,    null,   null )
                                )
                );
        failures.add
                (
                        new LogInOutSessionTest
                                (
                                        null,
                                        new LogInOutResponse( null,    null,    null,   null )
                                )
                );
    }

    /* --- internal classes --- */
    protected static final class LogInOutSessionTest
    {
        private final String              idSession;
        private final LogInOutResponse    response;

        /* --- constructors --- */
        public LogInOutSessionTest
            (
                    String              session,
                    LogInOutResponse    reply
            )
        {
            this.idSession  = session;
            this.response   = reply;
        }

        /* --- getters --- */
        public String getIdSession()
        {
            return this.idSession;
        }
        public LogInOutResponse getResponse()
        {
            return this.response;
        }

    }

}
