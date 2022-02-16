package net.alemas.oss.tools.eventscollectorclient;


import net.alemas.oss.tools.eventscollector.io.EventsTiming;
import net.alemas.oss.tools.eventscollector.io.timing.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Checks the client library correctness for timing events;
 *
 * Created by MASCHERPA on 27/05/2021.
 */
public class EventsCollectorTimingsTest extends EventsTiming
{
    /* --- logging --- */
    final private static Logger log = LoggerFactory.getLogger( EventsCollectorTimingsTest.class );

    /* --- properties --- */
    private static ServerContext   server;

    /* --- class handling --- */
    @BeforeClass
    public static void setUp() throws URISyntaxException
    {
        server = new ServerContext();
    }

    @AfterClass
    public static void tearDown()
    {
        if ( server != null )
        {
            server.close();
        }
    }

    /* --- library test --- */
    @Test
    public void GivenService_WhenConnectViaLibrary_ThenReplyAsExpected() throws Exception
    {
        log.info( "client - begin" );

        EventsCollectorTimers collector = new EventsCollectorTimers( server.getService() );

        /* --- check the collection is empty --- */
        log.info( "test empty collection - begin" );

        List< TimingResponse > list = collector.getEventsList();
        assertNotNull( list );
        assertTrue( list.isEmpty() );

        log.info( "test empty collection - end" );


		/* --- fill in the events --- */
        log.info( "post events - begin" );

        boolean posted;
        for ( TimingEvent event : events )
        {
            posted = collector.postEvent
                    (
                            event.getApplication(),
                            event.getId(),
                            event.getWhen(),
                            event.getElapsed()
                    );
            assertTrue( posted );
        }
        log.info( "post events - end" );

        /* --- get the events list --- */
        log.info( "get all events - begin" );
        checkListResult
                (
                        collector.getEventsList(),
                        responses
                );
        log.info( "get all events - end" );

        log.info( "client - end" );
    }

    @Test
    public void Failures() throws Exception
    {
        log.info( "post of incorrect events - begin" );

        boolean                 posted;
        EventsCollectorTimers  collector = new EventsCollectorTimers( server.getService() );

        for ( TimingEvent failure : failures )
        {
            posted = collector.postEvent
                    (
                            failure.getApplication(),
                            failure.getId(),
                            failure.getWhen(),
                            failure.getElapsed()
                    );
            assertFalse( posted );
        }
        log.info( "post of incorrect events - end" );
    }

}
