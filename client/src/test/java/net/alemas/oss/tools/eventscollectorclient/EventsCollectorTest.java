package net.alemas.oss.tools.eventscollectorclient;

import net.alemas.oss.tools.eventscollector.EventsCollectorApplication;
import net.alemas.oss.tools.eventscollector.EventsLogInOut;
import net.alemas.oss.tools.eventscollector.io.LogInOutResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.SocketUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Checks the library correctness against the service;
 *
 * Created by MASCHERPA on 19/02/2021.
 */
public class EventsCollectorTest extends EventsLogInOut
{
    /* --- logging --- */
    final private static Logger log = LoggerFactory.getLogger( EventsCollectorTest.class );

    /* --- static context --- */
    /**
     * remote service context;
     */
    private static ConfigurableApplicationContext   context;

    /**
     * the remote service: host name and port;
     */
    private static URI                              service;


    @BeforeClass
    public static void setUp() throws URISyntaxException
    {
        int port = setRandomPort( 9000, 9900 );

        context = new SpringApplicationBuilder( EventsCollectorApplication.class ).run();
        service = new URI( "http", null, "localhost", port, null, null, null );
    }
    public static int setRandomPort( int minPort, int maxPort )
    {
        int port;
        try
        {
            String userDefinedPort = System.getProperty( "server.port", System.getenv( "SERVER_PORT" ) );
            if ( StringUtils.isEmpty( userDefinedPort ) )
            {
                port = SocketUtils.findAvailableTcpPort( minPort, maxPort );
                System.setProperty( "server.port", String.valueOf( port ) );
                log.info( "Server port set to {}.", port) ;
            }
            else
            {
                port = Integer.valueOf( userDefinedPort );
                log.info( "Server port already set in environment as {}.", port) ;
            }
        }
        catch ( IllegalStateException e )
        {
            port = 8090;
            log.warn( "No port available in range {}-{}. Default embedded server configuration will be used.", minPort, maxPort );
        }
        return
                port;
    }

    @AfterClass
    public static void tearDown()
    {
        if ( context != null )
        {
            context.close();
        }
    }

    /* --- library test --- */
    @Test
    public void GivenService_WhenConnectViaLibrary_ThenReplyAsExpected() throws Exception
    {
        log.info( "client - begin" );

        EventsCollector collector = new EventsCollector( service );

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
                    EventsCollector         collector,
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
                    EventsCollector         collector,
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

        boolean             posted;
        EventsCollector collector = new EventsCollector( service );

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
