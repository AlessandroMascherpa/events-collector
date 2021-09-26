package net.alemas.oss.tools.eventscollectorclient;


import net.alemas.oss.tools.eventscollector.io.timing.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;
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
 * Client library to post timing events and enquiry the service.
 *
 * Created by MASCHERPA on 27/05/2021.
 */
public class EventsCollectorTimer extends EventsCollector
{
    /* --- constants --- */
    /**
     * service path;
     */
    private static final String PATH_BASE   = "/events/timing/";

    /* --- constructors --- */
    /**
     * Creates new connector to remote service for timing events;
     *
     * @param remote    the host name and port of the remote service;
     *
     * @throws MalformedURLException    if the URI is not correct;
     * @throws IllegalArgumentException if the URI was not defined;
     */
    public EventsCollectorTimer( URI remote )
            throws
                MalformedURLException,
                IllegalArgumentException
    {
        super( remote, PATH_BASE );
    }
    /**
     * Creates new connector to remote service for timing events;
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
    public EventsCollectorTimer
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
     * posts to remote service a timing event;
     * the event will be set with current date/time
     * as event timestamp;
     *
     * @param application    the application identifier;
     * @param id             the event identifier;
     * @param elapsed        the amount of time elapsed for the event;
     * @return true, if the remote service accepted the event; false, otherwise;
     */
    public boolean postEvent
        (
                String	application,
                String	id,
                double  elapsed
        )
    {
        return
                this.postEvent
                        (
                                application,
                                id,
                                LocalDateTime.now(),
                                elapsed
                        );
    }
    /**
     * posts to remote service a timing event;
     *
     * @param application    the application identifier;
     * @param id             the event identifier;
     * @param when           when the event occurred;
     * @param elapsed        the amount of time elapsed for the event;
     * @return true, if the remote service accepted the event; false, otherwise;
     */
    public boolean postEvent
        (
                String			application,
                String			id,
                LocalDateTime   when,
                double          elapsed
        )
    {
        String          url     = super.buildUrl();
        TimingEvent     event   = this.buildBody( application, id, when, elapsed );

        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            "posting timing event - end point '{}' - event: application: '{}', id: '{}', when: {} - begin",
                            url,
                            application,
                            id,
                            TimingEvent.convertDate( when )
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
                                        TimingEvent.class
                                )
                        .exchangeToMono
                                (
                                        response ->
                                                Mono.just
                                                        (
                                                                ( response != null )
                                                                &&
                                                                HttpStatus.NO_CONTENT.equals( response.statusCode() )
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
    private TimingEvent buildBody
            (
                    String			application,
                    String			id,
                    LocalDateTime   when,
                    double          elapsed
            )
    {
        return
                new TimingEvent
                        (
                                application,
                                id,
                                when,
                                elapsed
                        )
                ;
    }

    /**
     * lists all timing events grouped by its identifier;
     *
     * @return list of events grouped by identifier;
     */
    public List< TimingResponse > getEventsList()
    {
        return
                this.getEventsList( null, null, null );
    }
    /**
     * lists all timing events grouped by its identifier;
     * events are filtered by formal parameters;
     *
     * @param application   at which application the event must belong to;
     * @param after         events occurred after the given date;
     * @param before        events occurred before the given date;
     * @return list of events grouped by identifier;
     */
    public List< TimingResponse > getEventsList
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
                            "listing timing events - end point '{}' - begin",
                            url
                    );
        }
        List< TimingResponse > list =
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
                                                        ? response.bodyToFlux( TimingResponse.class )
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
                            "listing timing events - items returned: {} - end",
                            list.size()
                    );
        }
        return
                list;
    }

}
