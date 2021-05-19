package net.alemas.oss.tools.eventscollectorclient;

import net.alemas.oss.tools.eventscollector.EventsLogInOut;
import net.alemas.oss.tools.eventscollector.io.LogInOutResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Checks the client library correctness for logs in/out events;
 *
 * Created by MASCHERPA on 19/02/2021.
 */
public class EventsCollectorLoginsTest extends EventsLogInOut
{
    /* --- logging --- */
    final private static Logger log = LoggerFactory.getLogger( EventsCollectorLoginsTest.class );

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

        EventsCollectorLogins collector = new EventsCollectorLogins( server.getService() );

        /* --- check the collection is empty --- */
        log.info( "test empty collection - begin" );

        List< LogInOutResponse > list = collector.getEventsList();
        assertNotNull( list );
        assertTrue( list.isEmpty() );

        log.info( "test empty collection - end" );


		/* --- fill in the events --- */
        log.info( "post events - begin" );

        boolean posted;
        for ( LogInOutResponse response : responses )
        {
            posted = collector.postEvent
                    (
                            response.getUsername(),
                            response.getApplication(),
                            response.getDateLoggedIn(),
                            true
                    );
            assertTrue( posted );

            if ( response.getDateLoggedOut() != null )
            {
                posted = collector.postEvent
                        (
                                response.getUsername(),
                                response.getApplication(),
                                response.getDateLoggedOut(),
                                false
                        );
                assertTrue( posted );
            }
        }
        log.info( "post events - end" );

        /* --- get the events list --- */
        checkListResult
                (
                        collector.getEventsList(),
                        responses
                );

		/* --- check inserted events - all records --- */
        log.info( "get all events - begin" );

        checkList( collector, null, null, null, responses );

        log.info( "get all events - end" );

        /* --- check inserted events - filter by application --- */
        log.info( "get events filtered by application - begin" );

        checkList( collector, APP_DOT,  null, null, appDot );

        log.info( "get events filtered by application - end" );

        /* --- check inserted events - filter by date interval --- */
        log.info( "get events filtered by date interval - begin" );

        checkListByDate( collector, appDate );

        log.info( "get events filtered by date interval - end" );

        log.info( "client - end" );
    }
    private void checkListByDate
            (
                    EventsCollectorLogins       collector,
                    List< LogInOutResponse >    expected
            )
    {
        LocalDateTime   min;
        LocalDateTime   max;

        min = max = expected.get( 0 ).getDateLoggedIn();
        for ( LogInOutResponse response : expected )
        {
            LocalDateTime date = response.getDateLoggedIn();
            if ( date.compareTo( min ) < 0 )
            {
                min = date;
            }
            if ( date.compareTo( max ) > 0 )
            {
                max = date;
            }

            date = response.getDateLoggedOut();
            if ( date.compareTo( min ) < 0 )
            {
                min = date;
            }
            if ( date.compareTo( max ) > 0 )
            {
                max = date;
            }
        }
        checkList
                (
                        collector,
                        null,
                        min,
                        max,
                        expected
                );
    }
    private void checkList
            (
                    EventsCollectorLogins       collector,
                    String                      app,
                    LocalDateTime               after,
                    LocalDateTime               before,
                    List< LogInOutResponse >    expected
            )
    {
        checkListResult
                (
                        collector.getEventsList( app, after, before ),
                        expected
                );
    }

    @Test
    public void Failures() throws Exception
    {
        log.info( "post of incorrect events - begin" );

        boolean                 posted;
        EventsCollectorLogins   collector = new EventsCollectorLogins( server.getService() );

        for ( LogInOutResponse failure : failures )
        {
            posted = collector.postEvent
                    (
                            failure.getUsername(),
                            failure.getApplication(),
                            failure.getDateLoggedIn(),
                            true
                    );
            assertFalse( posted );

            posted = collector.postEvent
                    (
                            failure.getUsername(),
                            failure.getApplication(),
                            failure.getDateLoggedOut(),
                            false
                    );
            assertFalse( posted );
        }
        log.info( "post of incorrect events - end" );
    }

}
