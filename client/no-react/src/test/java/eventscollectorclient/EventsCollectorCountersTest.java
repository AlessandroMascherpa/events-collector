package eventscollectorclient;

import net.alemas.oss.tools.eventscollector.EventsCounter;
import net.alemas.oss.tools.eventscollector.io.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.CounterResponse;
import net.alemas.oss.tools.eventscollectorclient.EventsCollectorCounters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Checks the client library correctness for simple events;
 *
 * Created by MASCHERPA on 14/05/2021.
 */
public class EventsCollectorCountersTest extends EventsCounter
{
    /* --- logging --- */
    final private static Logger log = LoggerFactory.getLogger( EventsCollectorCountersTest.class );

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

        EventsCollectorCounters collector = new EventsCollectorCounters( server.getService() );

        /* --- check the collection is empty --- */
        log.info( "test empty collection - begin" );

        List< CounterResponse > list = collector.getEventsList();
        assertNotNull( list );
        assertTrue( list.isEmpty() );

        log.info( "test empty collection - end" );


		/* --- fill in the events --- */
        log.info( "post events - begin" );

        boolean posted;
        for ( CounterEvent event : events )
        {
            posted = collector.postEvent
                    (
                            event.getId(),
                            event.getWhen()
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
        EventsCollectorCounters collector = new EventsCollectorCounters( server.getService() );

        for ( CounterEvent failure : failures )
        {
            posted = collector.postEvent
                    (
                            failure.getId(),
                            failure.getWhen()
                    );
            assertFalse( posted );
        }
        log.info( "post of incorrect events - end" );
    }

}
