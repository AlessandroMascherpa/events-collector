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
public class EventsLogInOut extends EventsBase< LogInOutResponse >
{
    /* --- checking methods --- */
    @Override
    protected void checkListResult
        (
                List< LogInOutResponse > actual,
                List< LogInOutResponse > expected
        )
    {
        assertNotNull( actual );
        assertFalse( actual.isEmpty() );
        assertEquals( expected.size(), actual.size() );
        for ( int index = 0 ; index < expected.size() ; index ++ )
        {
            LogInOutResponse exp	= expected.get( index );
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

    protected static final List< LogInOutResponse > appDot      = new ArrayList<>();
    static
    {
        appDot.add( new LogInOutResponse( APP_DOT, USER_1, asDate( 2021, 2, 10, 12, 49, 3 ), asDate( 2021, 2, 10, 13, 9, 0 ) ) );
        appDot.add( new LogInOutResponse( APP_DOT, USER_1, asDate( 2021, 2, 10, 14, 39, 3 ), null ) );

        appDot.add( new LogInOutResponse( APP_DOT, USER_2, asDate( 2021, 2,  9, 14, 39, 3 ), null ) );
    }

    protected static final List< LogInOutResponse >	appNet      = new ArrayList<>();
    static
    {
        appNet.add( new LogInOutResponse( APP_NET, USER_1, asDate( 2021, 2, 10, 13, 49, 3 ), asDate( 2021, 2, 10, 14, 19, 0 ) ) );
        appNet.add( new LogInOutResponse( APP_NET, USER_1, asDate( 2021, 2, 10, 14, 38, 3 ), asDate( 2021, 2, 10, 14, 45, 3 ) ) );
        appNet.add( new LogInOutResponse( APP_NET, USER_1, asDate( 2021, 2, 10, 14, 40, 3 ), asDate( 2021, 2, 10, 15, 15, 3 ) ) );

        appNet.add( new LogInOutResponse( APP_NET, USER_1, asDate( 2021, 3, 12, 14, 38, 3 ), null ) );
    }

    protected static final List< LogInOutResponse >	appDate     = new ArrayList<>();
    static
    {
        appDate.add( new LogInOutResponse( APP_ORG, USER_1, asDate( 2021, 3, 10, 12, 49, 3 ), asDate( 2021, 3, 10, 13, 9, 0 ) ) );
        appDate.add( new LogInOutResponse( APP_ORG, USER_1, asDate( 2021, 3, 10, 14, 39, 3 ), asDate( 2021, 3, 10, 14, 45, 7 ) ) );
    }

    protected static final List< LogInOutResponse >	responses	= new ArrayList<>();
    static
    {
        responses.addAll( appDot );
        responses.addAll( appNet );
        responses.addAll( appDate );
    }

    protected final static List< LogInOutResponse >	failures	= new ArrayList<>();
    static
    {
        failures.add( new LogInOutResponse( null,    null,    null,   null ) );
        failures.add( new LogInOutResponse( "",      null,    null,   null ) );
        failures.add( new LogInOutResponse( "",      "",      null,   null ) );
        failures.add( new LogInOutResponse( APP_NET, USER_2,  null,   null ) );
        failures.add( new LogInOutResponse( APP_NET, USER_3,  null,   null ) );
    }

}
