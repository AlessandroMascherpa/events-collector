package net.alemas.oss.tools.eventscollectorclient;


import net.alemas.oss.tools.eventscollector.io.Base;
import net.alemas.oss.tools.eventscollector.io.counter.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.counter.CounterResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
     * @param application    the application identifier;
     * @param id             the event identifier;
     * @return true, if the remote service accepted the event; false, otherwise;
     */
    public boolean postEvent
        (
                String	application,
                String	id
        )
    {
        return
                this.postEvent
                        (
                                application,
                                id,
                                LocalDateTime.now()
                        );
    }
    /**
     * posts to remote service a single event;
     *
     * @param application    the application identifier;
     * @param id             the event identifier;
     * @param when           when the event occurred;
     * @return true, if the remote service accepted the event; false, otherwise;
     */
    public boolean postEvent
        (
                String			application,
                String			id,
                LocalDateTime   when
        )
    {
        String          url     = super.buildUrl();
        CounterEvent    event   = this.buildBody( application, id, when );

        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            "posting single event - end point '{}' - event: application: '{}', id: '{}', when: {} - begin",
                            url,
                            application,
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
                        .contentType
                                (
                                        MediaType.APPLICATION_JSON
                                )
                        .body
                                (
                                        Mono.just( event ),
                                        CounterEvent.class
                                )
                        .exchangeToMono
                                (
                                        response ->
                                                Mono.just
                                                        (
                                                                ( response != null )
                                                                &&
                                                                HttpStatus.CREATED.equals( response.statusCode() )
                                                        )
                                )
                        .block()
                ;
        boolean response = ( reply != null ) && reply;      // by SpotBugs

        if ( log.isDebugEnabled() )
        {
            log.debug( "posting event - successful posted: {} - end", response );
        }
        return
                response;
    }
    private CounterEvent buildBody
            (
                    String			application,
                    String			id,
                    LocalDateTime   when
            )
    {
        return
                new CounterEvent
                        (
                                application,
                                id,
                                when
                        )
                ;
    }

    /**
     * lists all single events grouped by its identifier;
     *
     * @return list of events grouped by identifier;
     */
    public List< CounterResponse > getEventsList()
    {
        return
                this.getEventsList( null, null, null );
    }
    /**
     * lists all single events grouped by its identifier;
     * events are filtered by formal parameters;
     *
     * @param application   at which application the event must belong to;
     * @param after         events occurred after the given date;
     * @param before        events occurred before the given date;
     * @return list of events grouped by identifier;
     */
    public List< CounterResponse > getEventsList
        (
                String        application,
                LocalDateTime after,
                LocalDateTime before
        )
    {
        String  url     = super.buildUrl( application, after, before );

        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            "listing simple events - end point '{}' - begin",
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
                                                ( ( response != null ) && HttpStatus.OK.equals( response.statusCode() ) )
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
                            "listing single events - items returned: {} - end",
                            ( ( list != null ) ? list.size() : 0 )
                    );
        }
        return
                list;
    }

}
