package net.alemas.oss.tools.eventscollector;


import net.alemas.oss.tools.eventscollector.configuration.Properties;
import net.alemas.oss.tools.eventscollector.io.timing.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDateTime;
import java.util.List;


@ExtendWith( SpringExtension.class )
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class TimerEventsCollectorApplicationTests extends EventsTiming
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
                .uri( this.getUrlPath())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList( TimingResponse.class )
                .hasSize( 0 )
        ;
        log.info( "test empty collection - end" );

        /* --- fill in the events --- */
        log.info( "post events - begin" );
        for ( TimingEvent event : events )
        {
            this.webTestClient
                    .post()
                    .uri( this.getUrlPath() )
                    .contentType( MediaType.APPLICATION_FORM_URLENCODED )
                    .body
                            (
                                    buildBody( event )
                            )
                    .exchange()
                    .expectStatus().isNoContent()
            ;
        }
        log.info( "post events - end" );

		/* --- check inserted events - all records --- */
        log.info( "get all events - begin" );

        this.checkList( responses );

        log.info( "get all events - end" );

        log.info( "server - end" );
    }
    private void checkList( List< TimingResponse > expected )
    {
        String                      url     = this.getUrlPath();

        List< TimingResponse >      actual  = this.webTestClient
                .get()
                .uri( url )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList( TimingResponse.class )
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

        for ( TimingEvent event : failures )
        {
            this.webTestClient
                    .post()
                    .uri( this.getUrlPath() )
                    .contentType( MediaType.APPLICATION_FORM_URLENCODED )
                    .body
                            (
                                    buildBody( event )
                            )
                    .exchange()
                    .expectStatus().isBadRequest()
            ;
        }

        log.info( "post of incorrect events - end" );
    }

    /* --- API call payload --- */
    private BodyInserters.FormInserter< String > buildBody( TimingEvent event )
    {
        MultiValueMap< String, String > body = new LinkedMultiValueMap<>();
        if ( event != null )
        {
            String application = event.getApplication();
            if ( ( application != null ) && ( application.length() > 0 ) )
            {
                body.set( "application", application );
            }

            String id = event.getId();
            if ( ( id != null ) && ( id.length() > 0 ) )
            {
                body.set( "id", id );
            }

            LocalDateTime when = event.getWhen();
            if ( when != null )
            {
                body.set( "when", TimingEvent.convertDate( when ) );
            }

            body.set( "elapsed", String.valueOf( event.getElapsed() ) );
        }
        return
                BodyInserters
                        .fromFormData( body );
    }

    /* --- URL related methods --- */
    /**
     * trailing part of API path;
     */
    private static final String		URL_PATH	= "/events/timings/";

    @Autowired
    private Properties              properties;

    /**
     * get the full API path;
     *
     * @return the API path;
     */
    private String  getUrlPath()
    {
        return
                this.properties.getBasePath() + URL_PATH;
    }

}
