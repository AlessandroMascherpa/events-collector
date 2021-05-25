package net.alemas.oss.tools.eventscollectorclient;


import net.alemas.oss.tools.eventscollector.io.Base;
import net.alemas.oss.tools.eventscollector.io.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.CounterResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;



/**
 * Client library to post single events and enquiry the service.
 *
 * Created by MASCHERPA on 14/05/2021.
 */
public class EventsCollectorCounters extends EventsCollector
{
    /* --- constants --- */
    /**
     * service path;
     */
    private static final String PATH_BASE   = "/events/counter/";

    /* --- constructors --- */
    /**
     * Creates new connector to remote service for single events;
     *
     * @param remote    the host name and port of the remote service;
     *
     * @throws MalformedURLException    if the URI is not correct;
     * @throws IllegalArgumentException if the URI was not defined;
     */
    public EventsCollectorCounters( URI remote )
            throws
                MalformedURLException,
                IllegalArgumentException
    {
        super( remote, PATH_BASE );
    }
    /**
     * Creates new connector to remote service for single events;
     *
     * @param remote            the host name and port of the remote service;
     * @param connections       maximum connections per connection pool;
     * @param acquire           maximum acquire time per connection (in seconds);
     * @param timeOutConnection connection time out (in milliseconds);
     * @param timeOutRead       read time out (in seconds);
     * @param timeOutWrite      write time out (in seconds);
     *
     * @throws MalformedURLException    if the URI is not correct;
     * @throws IllegalArgumentException if the URI was not defined;
     */
    public EventsCollectorCounters
        (
                URI     remote,
                int     connections,
                int     acquire,
                int     timeOutConnection,
                int     timeOutRead,
                int     timeOutWrite
        )
            throws
                MalformedURLException,
                IllegalArgumentException
    {
        super( remote, PATH_BASE, connections, acquire, timeOutConnection, timeOutRead, timeOutWrite );
    }

    /* --- connectors --- */
    /**
     * posts to remote service a single event;
     * the event will be set with current date/time
     * as event timestamp;
     *
     * @param id    the event identifier;
     * @return true, if the remote service accepted the event; false, otherwise;
     */
    public boolean postEvent
        (
                String	id
        )
    {
        return
                this.postEvent
                        (
                                id,
                                LocalDateTime.now()
                        );
    }
    /**
     * posts to remote service a single event;
     *
     * @param id    the event identifier;
     * @param when  when the event occurred;
     * @return true, if the remote service accepted the event; false, otherwise;
     */
    public boolean postEvent
        (
                String			id,
                LocalDateTime   when
        )
    {
        String                                  url     = this.url.build().toUriString();
        BodyInserters.FormInserter< String >    body    = this.buildBody( id, when );

        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            "posting single event - end point '{}' - event: id: '{}', when: {} - begin",
                            url,
                            id,
                            Base.convertDate( when )
                    );
        }
        Boolean reply =
                this
                        .client
                        .baseUrl( url )
                        .build()
                        .post()
                        .contentType( MediaType.APPLICATION_FORM_URLENCODED )
                        .body
                                (
                                        body
                                )
                        .exchangeToMono
                                (
                                        response ->
                                                Mono.just
                                                        (
                                                                response.statusCode().equals( HttpStatus.NO_CONTENT )
                                                        )
                                )
                        .block()
                ;
        if ( log.isDebugEnabled() )
        {
            log.debug( "posting event - successful posted: {} - end", reply );
        }
        return
                reply;
    }
    private BodyInserters.FormInserter< String > buildBody
            (
                    String			id,
                    LocalDateTime   when
            )
    {
        MultiValueMap< String, String > body = new LinkedMultiValueMap<>();

        if ( ( id != null ) && ( id.length() > 0 ) )
        {
            body.set( "id", id );
        }
        if ( when != null )
        {
            body.set( "when", CounterEvent.convertDate( when ) );
        }
        return
                BodyInserters
                        .fromFormData( body );
    }

    /**
     * lists all single events grouped by its identifier;
     *
     * @return list of events grouped by identifier;
     */
    public List< CounterResponse > getEventsList()
    {
        String  url = this.url.build().toUriString();

        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            "listing events - end point '{}' - begin",
                            url
                    );
        }
        List< CounterResponse > list =
                this
                        .client
                        .baseUrl( url )
                        .build()
                        .get()
                        .acceptCharset( StandardCharsets.UTF_8 )
                        .accept( MediaType.APPLICATION_JSON )
                        .ifNoneMatch( "*" )
                        .ifModifiedSince( ZonedDateTime.now() )
                        .exchangeToFlux
                                (
                                        response ->
                                                response.statusCode().equals( HttpStatus.OK )
                                                        ? response.bodyToFlux( CounterResponse.class )
                                                        : null
                                )
                        .collectList()
                        .block
                                (
                                        Duration.ofMinutes( 7 )
                                )
                ;
        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            "listing events - items returned: {} - end",
                            list.size()
                    );
        }
        return
                list;
    }

}
