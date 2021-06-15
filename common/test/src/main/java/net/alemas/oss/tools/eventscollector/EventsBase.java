package net.alemas.oss.tools.eventscollector;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Class with common payloads for testing.
 *
 * Created by MASCHERPA on 11/05/2021.
 */
public abstract class EventsBase< Response >
{
    /* --- payload values --- */
    protected static final String					APP_DOT		= "app-dot";
    protected static final String					APP_NET		= "app-net";
    protected static final String					APP_ORG		= "app-org";


    /* --- payload methods --- */
    protected static LocalDateTime asDate( int year, int month, int day, int hours, int minutes, int seconds )
    {
        return
                LocalDateTime.of( year, month,day, hours, minutes, seconds, 0 )
                ;
    }


    /* --- checking methods --- */
    protected void checkListResult
        (
                List< Response > actual,
                List< Response > expected
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
