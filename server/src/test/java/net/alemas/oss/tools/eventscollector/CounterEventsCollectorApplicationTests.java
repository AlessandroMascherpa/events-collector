package net.alemas.oss.tools.eventscollector;


import net.alemas.oss.tools.eventscollector.configuration.EndpointsPaths;
import net.alemas.oss.tools.eventscollector.configuration.Properties;
import net.alemas.oss.tools.eventscollector.io.events.TestEventsCounter;
import net.alemas.oss.tools.eventscollector.io.counter.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.out.EventsCounter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;


@ExtendWith( SpringExtension.class )
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class CounterEventsCollectorApplicationTests extends TestEventsCounter
{
    /* --- logging --- */
    final private static Logger log = LoggerFactory.getLogger( CounterEventsCollectorApplicationTests.class );

    /* --- properties --- */
    // Spring Boot will create a `WebTestClient` for you,
    // already configure and ready to issue requests against "localhost:RANDOM_PORT"
    @Autowired
    private WebTestClient webTestClient;

	/* --- testing methods --- */
    @Test
    public void GivenEmptyStore_WhenStoreEvents_ThenGetStoredEvents()
    {
        log.info( "server - begin" );

		/* --- test empty collection --- */
        log.info( "test empty collection - begin" );
        this.webTestClient
                .get()
                .uri( this.getUrlPath() )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList( EventsCounter.class )
                .hasSize( 0 )
        ;
        log.info( "test empty collection - end" );

        /* --- fill in the events --- */
        log.info( "post events - begin" );
        for ( CounterEvent event : events )
        {
            this.webTestClient
                    .post()
                    .uri( this.getUrlPath() )
                    .contentType( MediaType.APPLICATION_JSON )
                    .body
                            (
                                    Mono.just( event ),
                                    CounterEvent.class
                            )
                    .exchange()
                    .expectStatus().isCreated()
            ;
        }
        log.info( "post events - end" );

		/* --- check inserted events - all records --- */
        log.info( "get all events - begin" );

        this.checkList( null, responses );

        log.info( "get all events - end" );

        /* --- check inserted events - filter by application --- */
        log.info( "get events filtered by application - begin" );

        checkList( "application=" + APP_DOT, responsesDot );

        log.info( "get events filtered by application - end" );


        log.info( "server - end" );
    }
    private void checkList( String query, List< EventsCounter > expected )
    {
        String                      url     = this.getUrlPath();
        if ( ( query != null ) && ( ! "".equals( query ) ) )
        {
            url += '?' + query;
            log.info( "query parameters: '" + query + '\'' );
        }

        List< EventsCounter >     actual  = this.webTestClient
                .get()
                .uri( url )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList( EventsCounter.class )
                .hasSize( expected.size() )
                .returnResult()
                .getResponseBody()
                ;

        this.checkListResult( actual, expected );
    }

    @Test
    public void Failures()
    {
        log.info( "post of incorrect events - begin" );

        for ( CounterEvent event : failures )
        {
            this.webTestClient
                    .post()
                    .uri( this.getUrlPath() )
                    .contentType( MediaType.APPLICATION_JSON )
                    .body
                            (
                                    Mono.just( event ),
                                    CounterEvent.class
                            )
                    .exchange()
                    .expectStatus().isBadRequest()
            ;
        }

        log.info( "post of incorrect events - end" );
    }

    /* --- URL related methods --- */
    /**
     * trailing part of API path;
     */
    private static final String		URL_PATH	= EndpointsPaths.FULL_PATH_COUNTER_EVENT;

    @Autowired
    private Properties              properties;

    /**
     * get the full API path to store event;
     *
     * @return the API path;
     */
    private String  getUrlPath()
    {
        return
                this.properties.getBasePath() + URL_PATH;
    }

}
