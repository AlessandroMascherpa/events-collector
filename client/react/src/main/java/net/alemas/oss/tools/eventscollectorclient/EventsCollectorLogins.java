package net.alemas.oss.tools.eventscollectorclient;


import net.alemas.oss.tools.eventscollector.configuration.EndpointsPaths;
import net.alemas.oss.tools.eventscollector.io.Base;
import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutEvent;
import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutResponse;
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
 * Client library to post logs in/out events and enquiry the service.
 *
 * Created by MASCHERPA on 14/05/2021.
 */
public class EventsCollectorLogins extends EventsCollector
{
    /* --- constants --- */
    /**
     * service path;
     */
    private static final String PATH_BASE   = EndpointsPaths.FULL_PATH_LOGIN_EVENT;

    /* --- constructors --- */
    /**
     * Creates new connector to remote service for single events;
     *
     * @param remote    the host name and port of the remote service;
     *
     * @throws MalformedURLException    if the URI is not correct;
     * @throws IllegalArgumentException if the URI was not defined;
     */
    public EventsCollectorLogins( URI remote )
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
    public EventsCollectorLogins
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
     * posts to remote service a log in/out event set the current date/time
     * as event timestamp;
     *
     * @param application    the application identifier;
     * @param user           the username;
     * @param idSession      the session identifier;
     * @param in             if the end user logged in;
     * @return true, if the remote service accepted the event; false, otherwise;
     */
    public boolean postEvent
        (
                String			application,
                String			user,
                String          idSession,
                boolean			in
        )
    {
        return
                this.postEvent
                        (
                                user,
                                application,
                                idSession,
                                LocalDateTime.now(),
                                in
                        );
    }
    /**
     * posts to remote service a log in/out event;
     *
     * @param application    the application identifier;
     * @param user           the username;
     * @param idSession      the session identifier;
     * @param when           when the event occurred;
     * @param in             if the end user logged in;
     * @return true, if the remote service accepted the event; false, otherwise;
     */
    public boolean postEvent
        (
                String			application,
                String			user,
                String          idSession,
                LocalDateTime   when,
                boolean			in
        )
    {
        String          url     = super.buildUrl();
        LogInOutEvent   event   = this.buildBody( user, application, idSession, when, in );

        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            "posting log in/out event - end point '{}' - event: application: '{}', user: '{}', session: '{}', when: {}, in: {} - begin",
                            url,
                            application,
                            user,
                            idSession,
                            Base.convertDate( when ),
                            String.valueOf( in )
                    );
        }
        Boolean reply =
                this
                        .client
                        .baseUrl( url )
                        .build()
                        .post()
                        .contentType( MediaType.APPLICATION_JSON )
                        .body
                                (
                                        Mono.just( event ),
                                        LogInOutEvent.class
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
    private LogInOutEvent buildBody
            (
                    String			user,
                    String			application,
                    String          idSession,
                    LocalDateTime   when,
                    boolean			in
            )
    {
        return
                new LogInOutEvent
                        (
                                application,
                                user,
                                idSession,
                                when,
                                in
        )
                ;
    }

    /**
     * lists all log in/out events stored by service;
     *
     * @return list of events grouped by username;
     */
    public List< LogInOutResponse > getEventsList()
    {
        return
                this.getEventsList( null, null, null );
    }
    /**
     * lists all log in/out events stored by service;
     * events are filtered by formal parameters;
     *
     * @param application   at which application the event must belong to;
     * @param after         events occurred after the given date;
     * @param before        events occurred before the given date;
     * @return list of events grouped by username;
     */
    public List< LogInOutResponse > getEventsList
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
                            "listing events - end point '{}' - filter by: application: '{}', after: {}, before: {} - begin",
                            url,
                            application,
                            Base.convertDate( after ),
                            Base.convertDate( before )
                    );
        }
        List< LogInOutResponse > list =
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
                                                        ? response.bodyToFlux( LogInOutResponse.class )
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
                            ( ( list != null ) ? list.size() : 0 )
                    );
        }
        return
                list;
    }

}
