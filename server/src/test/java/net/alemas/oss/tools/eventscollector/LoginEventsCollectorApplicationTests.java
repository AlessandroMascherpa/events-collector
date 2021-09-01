package net.alemas.oss.tools.eventscollector;

import net.alemas.oss.tools.eventscollector.configuration.Properties;
import net.alemas.oss.tools.eventscollector.io.loginout.LogInOut;
import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutEvent;
import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutResponse;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;



@ExtendWith( SpringExtension.class )
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
class LoginEventsCollectorApplicationTests extends EventsLogInOut
{
	/* --- logging --- */
	final private static Logger log = LoggerFactory.getLogger( LoginEventsCollectorApplicationTests.class );

	/* --- properties --- */
	// Spring Boot will create a `WebTestClient` for you,
	// already configure and ready to issue requests against "localhost:RANDOM_PORT"
	@Autowired
	private WebTestClient webTestClient;

	/* --- methods --- */
	@Test
	public void GivenThreeRecords_WhenAskList_ThenCorrectListGot()
	{
        log.info( "server - begin" );

		/* --- test empty collection --- */
        log.info( "test empty collection - begin" );

		this.webTestClient
				.get()
				.uri( this.getUrlPath( "0" ) )
				.accept( MediaType.APPLICATION_JSON )
				.exchange()
				.expectStatus().isNotFound()
		;

		this.webTestClient
				.get()
				.uri( this.getUrlPath( "1" ) )
				.accept( MediaType.APPLICATION_JSON )
				.exchange()
				.expectStatus().isNotFound()
		;

		this.webTestClient
				.get()
				.uri( this.getUrlPath( "-1" ) )
                .accept( MediaType.APPLICATION_JSON )
				.exchange()
				.expectStatus().isNotFound()
		;

		this.webTestClient
				.get()
				.uri( this.getUrlPath())
				.exchange()
				.expectStatus().isOk()
				.expectBodyList( LogInOutResponse.class )
				.hasSize( 0 )
		;

        log.info( "test empty collection - end" );

		/* --- fill in the events --- */
        log.info( "post events - begin" );
		LogInOutEvent event;
		for ( LogInOutResponse response : responses )
		{
            event = getLogIn( response );
			this.webTestClient
					.post()
					.uri( this.getUrlPath() )
					.contentType( MediaType.APPLICATION_JSON )
					.body
                            (
                                    Mono.just( event ),
                                    LogInOutResponse.class
                            )
                    .exchange()
                    .expectStatus().isNoContent()
			;


			event = getLogOut( response );
			if ( event != null )
			{
				this.webTestClient
						.post()
						.uri( this.getUrlPath() )
						.contentType( MediaType.APPLICATION_JSON )
						.body
                                (
                                        Mono.just( event ),
                                        LogInOutResponse.class
                                )
						.exchange()
						.expectStatus().isNoContent()
				;
			}
		}
        log.info( "post events - end" );

		/* --- check inserted events - single record --- */
        log.info( "get single event - begin" );
		LogInOutEvent result = this.webTestClient
				.get()
				.uri( this.getUrlPath( "0" ) )
				.accept( MediaType.APPLICATION_JSON )
				.exchange()
				.expectStatus().isOk()
				.expectBody( LogInOutEvent.class )
				.returnResult()
				.getResponseBody()
				;
		assertEquals( getLogIn( responses.get( 0 ) ), result );
        log.info( "get single event - end" );

		/* --- check inserted events - all records --- */
        log.info( "get all events - begin" );

        checkList( null, responses );

        log.info( "get all events - end" );

        /* --- check inserted events - filter by application --- */
        log.info( "get events filtered by application - begin" );

        checkList( "application=" + APP_DOT, appDot );

        log.info( "get events filtered by application - end" );

        /* --- check inserted events - filter by date interval --- */
        log.info( "get events filtered by date interval - begin" );

        checkListByDate( appDate );

        log.info( "get events filtered by date interval - end" );


        log.info( "server - end" );
	}
    private void checkListByDate( List< LogInOutResponse > expected )
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
                        "after=" + LogInOut.convertDate( min ) + '&' + "before=" + LogInOut.convertDate( max ),
                        expected
                );
    }
    private void checkList( String query, List< LogInOutResponse > expected )
    {
        String  url = this.getUrlPath();
        if ( ( query != null ) && ( ! "".equals( query ) ) )
        {
            url += '?' + query;
            log.info( "query parameters: '" + query + '\'' );
        }

        List< LogInOutResponse > list = this.webTestClient
                .get()
                .uri( url )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList( LogInOutResponse.class )
                .hasSize( expected.size() )
                .returnResult()
                .getResponseBody()
                ;

        checkListResult( list, expected );
    }

    @Test
    public void Failures()
    {
		log.info( "post of incorrect events - begin" );

        LogInOutEvent event;
        for ( LogInOutResponse response : failures )
        {
            event = getLogIn( response );
            this.webTestClient
                    .post()
                    .uri( this.getUrlPath() )
                    .contentType( MediaType.APPLICATION_JSON )
                    .body
                            (
                                    Mono.just( event ),
                                    LogInOutResponse.class
                            )
                                    .exchange()
                    .expectStatus().isBadRequest()
            ;

            event = getLogOut( response );
            this.webTestClient
                    .post()
                    .uri( this.getUrlPath() )
                    .contentType( MediaType.APPLICATION_JSON )
                    .body
                            (
                                    ( event != null ) ? Mono.just( event ) : Mono.empty(),
                                    LogInOutResponse.class
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
    private static final String		URL_PATH	= "/events/log-in-out/";

    @Autowired
    private Properties              properties;

    /**
     * get the full API path without the trailing row number;
     *
     * @return the API path;
     */
    private String  getUrlPath()
    {
        return
                this.getUrlPath( null );
    }
    /**
     * get the full API path with the trailing row number;
     *
     * @param row    the row number;
     * @return the API path;
     */
    private String  getUrlPath( String row )
    {
        String path = this.properties.getBasePath() + URL_PATH;
        if ( row != null )
        {
            path = path + row;
        }
        return
                path;
    }


	/* --- payload methods --- */
	public static LogInOutEvent  getLogIn( LogInOutResponse response )
	{
		return
                new LogInOutEvent
                        (
                                response.getApplication(),
                                response.getUsername(),
                                response.getDateLoggedIn(),
                                true
                        )
				;
	}
	public static LogInOutEvent  getLogOut( LogInOutResponse response )
	{
		LogInOutEvent	payload	= null;
		LocalDateTime	out		= response.getDateLoggedOut();
		if ( out != null )
		{
			payload	= new LogInOutEvent
                    (
                            response.getApplication(),
                            response.getUsername(),
                            out,
                            false
                    );
		}
		return
				payload
				;
	}

}
