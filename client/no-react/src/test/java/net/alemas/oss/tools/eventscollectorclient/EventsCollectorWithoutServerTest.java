package net.alemas.oss.tools.eventscollectorclient;


import net.alemas.oss.tools.eventscollector.io.EventsLogInOut;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;


/**
 * Test suite to check client library behaviour without the remote service;
 *
 * Created by MASCHERPA on 23/02/2021.
 */
public class EventsCollectorWithoutServerTest extends EventsLogInOut
{
    /* --- logging --- */
    final private static Logger log = LoggerFactory.getLogger( EventsCollectorWithoutServerTest.class );

    /* --- static context --- */
    /**
     * the remote service: host name and port;
     */
    private static URI service;

    static
    {
        try
        {
            service = new URI( "http", null, "localhost", 8090, null, null, null );
        }
        catch ( URISyntaxException e )
        {
            log.error( "URI malformed", e );
        }
    }

    /* --- library test --- */
    @Test
    public void GivenServiceNotRunning_WhenConnectViaLibraryEventLogins_ThenFailedWithinExpectedTime() throws Exception
    {
        boolean                 posted;
        long                    start;
        EventsCollectorLogins   collector = new EventsCollectorLogins( service );

        /* --- attempt to post an event - with date/time --- */
        start = System.currentTimeMillis();
        try
        {
            posted = collector.postEvent
                    (
                            "user-1",
                            "app-1",
                            "session-1",
                            asDate( 2021, 2, 10, 14, 39, 3 ),
                            true
                    );
            assertFalse( posted );
        }
        catch ( WebClientRequestException e )
        {
            assertElapsedTime( start );
        }
        catch ( Exception e )
        {
            fail( "Unexpected exception: " + e.getMessage() );
        }

        /* --- attempt to post an event - without date/time --- */
        start = System.currentTimeMillis();
        try
        {
            posted = collector.postEvent
                    (
                            "user-1",
                            "app-1",
                            "session-1",
                            true
                    );
            assertFalse( posted );
        }
        catch ( WebClientRequestException e )
        {
            assertElapsedTime( start );
        }
        catch ( Exception e )
        {
            fail( "Unexpected exception: " + e.getMessage() );
        }

        /* --- attempt to list events --- */
        start = System.currentTimeMillis();
        try
        {
            collector.getEventsList();
        }
        catch ( WebClientRequestException e )
        {
            assertElapsedTime( start );
        }
        catch ( Exception e )
        {
            fail( "Unexpected exception: " + e.getMessage() );
        }

    }

    @Test
    public void GivenServiceNotRunning_WhenConnectViaLibraryEventSingle_ThenFailedWithinExpectedTime() throws Exception
    {
        boolean                 posted;
        long                    start;
        EventsCollectorCounters collector = new EventsCollectorCounters( service );

        /* --- attempt to post an event - with date/time --- */
        start = System.currentTimeMillis();
        try
        {
            posted = collector.postEvent
                    (
                            "app-1",
                            "id-1",
                            asDate( 2021, 5, 14, 23, 3, 58 )
                    );
            assertFalse( posted );
        }
        catch ( WebClientRequestException e )
        {
            assertElapsedTime( start );
        }
        catch ( Exception e )
        {
            fail( "Unexpected exception: " + e.getMessage() );
        }

        /* --- attempt to post an event - without date/time --- */
        start = System.currentTimeMillis();
        try
        {
            posted = collector.postEvent
                    (
                            "app-1",
                            "id-2"
                    );
            assertFalse( posted );
        }
        catch ( WebClientRequestException e )
        {
            assertElapsedTime( start );
        }
        catch ( Exception e )
        {
            fail( "Unexpected exception: " + e.getMessage() );
        }

        /* --- attempt to list events --- */
        start = System.currentTimeMillis();
        try
        {
            collector.getEventsList();
        }
        catch ( WebClientRequestException e )
        {
            assertElapsedTime( start );
        }
        catch ( Exception e )
        {
            fail( "Unexpected exception: " + e.getMessage() );
        }
    }

    @Test
    public void GivenServiceNotRunning_WhenConnectViaLibraryEventTiming_ThenFailedWithinExpectedTime() throws Exception
    {
        boolean                 posted;
        long                    start;
        EventsCollectorTimers   collector = new EventsCollectorTimers( service );

        /* --- attempt to post an event - with date/time --- */
        start = System.currentTimeMillis();
        try
        {
            posted = collector.postEvent
                    (
                            "app-1",
                            "id-1",
                            asDate( 2021, 5, 14, 23, 3, 58 ),
                            0.0D
                    );
            assertFalse( posted );
        }
        catch ( WebClientRequestException e )
        {
            assertElapsedTime( start );
        }
        catch ( Exception e )
        {
            fail( "Unexpected exception: " + e.getMessage() );
        }

        /* --- attempt to post an event - without date/time --- */
        start = System.currentTimeMillis();
        try
        {
            posted = collector.postEvent
                    (
                            "app-1",
                            "id-2",
                            0.0D
                    );
            assertFalse( posted );
        }
        catch ( WebClientRequestException e )
        {
            assertElapsedTime( start );
        }
        catch ( Exception e )
        {
            fail( "Unexpected exception: " + e.getMessage() );
        }

        /* --- attempt to list events --- */
        start = System.currentTimeMillis();
        try
        {
            collector.getEventsList();
        }
        catch ( WebClientRequestException e )
        {
            assertElapsedTime( start );
        }
        catch ( Exception e )
        {
            fail( "Unexpected exception: " + e.getMessage() );
        }
    }

    /* --- static properties --- */
    private static final long   CONNECTION_TIMEOUT  = 2500L;

    private static void assertElapsedTime( long start )
    {
        long elapsed = System.currentTimeMillis() - start;
        assertTrue
                (
                        "Connection failed after " + elapsed + "msec, expected failure within " + CONNECTION_TIMEOUT + "msec.",
                        ( elapsed < CONNECTION_TIMEOUT )
                );
    }
}
