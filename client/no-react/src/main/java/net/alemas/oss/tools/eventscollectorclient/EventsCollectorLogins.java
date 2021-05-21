package net.alemas.oss.tools.eventscollectorclient;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.alemas.oss.tools.eventscollector.io.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.LogInOut;
import net.alemas.oss.tools.eventscollector.io.LogInOutResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Client library to post logs in/out events and enquiry the service.
 *
 * Created by MASCHERPA on 21/05/2021.
 */
public class EventsCollectorLogins extends EventsCollector
{
    /* --- constants --- */
    /**
     * service path;
     */
    private static final String PATH_BASE   = "/events/logs-in-out/";

    /* --- properties --- */
    protected final JsonMapper mapper =
            JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
            ;

    /* --- constructors --- */
    /**
     * Creates new connector to remote service for log in/out events;
     *
     * @param remote    the host name and port of the remote service;
     *
     * @throws URISyntaxException       if the URI is not correct;
     * @throws IllegalArgumentException if the URI was not defined;
     */
    public EventsCollectorLogins( URI remote )
            throws
                URISyntaxException, IllegalArgumentException
    {
        super( remote, PATH_BASE );
    }
    /**
     * Creates new connector to remote service for log in/out events;
     *
     * @param remote            the host name and port of the remote service;
     * @param timeoutConnection connection timeout (in milliseconds);
     * @param timeoutRequest    request timeout (in milliseconds);
     * @param timeoutSocket     socket timeout (in milliseconds);
     *
     * @throws IllegalArgumentException if the URI was not defined;
     */
    public EventsCollectorLogins
        (
                URI     remote,
                int     timeoutConnection,
                int     timeoutRequest,
                int     timeoutSocket
        )
            throws
                URISyntaxException, IllegalArgumentException
    {
        super( remote, PATH_BASE, timeoutConnection, timeoutRequest, timeoutSocket );
    }

    /* --- connectors --- */
    /**
     * posts to remote service a log in/out event set the current date/time
     * as event timestamp;
     *
     * @param user           the username;
     * @param application    the application identifier;
     * @param in             if the end user logged in;
     * @return true, if the remote service accepted the event; false, otherwise;
     */
    public boolean postEvent
        (
                String			user,
                String			application,
                boolean			in
        )
    {
        return
                this.postEvent
                        (
                                user,
                                application,
                                LocalDateTime.now(),
                                in
                        );
    }
    /**
     * posts to remote service a log in/out event;
     *
     * @param user           the username;
     * @param application    the application identifier;
     * @param when           when the event occurred;
     * @param in             if the end user logged in;
     * @return true, if the remote service accepted the event; false, otherwise;
     */
    public boolean postEvent
        (
                String			user,
                String			application,
                LocalDateTime   when,
                boolean			in
        )
    {
        boolean reply;
        String  date    = LogInOut.convertDate( when );
        String  banner  = "posting single event - end point '" + this.remote.toString() + "' - event: username: '" + user + "', application: '" + application + "', when: " + date + ", in: " + in + " - ";
        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            banner + "begin"
                    );
        }
        try
        {
			/* --- prepare form --- */
            List< NameValuePair > encoded		= new ArrayList<>();
            encoded.add
                    (
                            new BasicNameValuePair
                                    (
                                            "username",
                                            user
                                    )
                    );
            encoded.add
                    (
                            new BasicNameValuePair
                                    (
                                            "application",
                                            application
                                    )
                    );
            encoded.add
                    (
                            new BasicNameValuePair
                                    (
                                            "when",
                                            CounterEvent.convertDate( when )
                                    )
                    );
            encoded.add
                    (
                            new BasicNameValuePair
                                    (
                                            "in",
                                            String.valueOf( in )
                                    )
                    );

			/* --- execute call and check reply --- */
            int status = super.postEvent( encoded );

            reply = ( status == HttpStatus.SC_NO_CONTENT );

            if ( log.isInfoEnabled() )
            {
                log.info
                        (
                                banner + ( reply ? "stored" : ( "failed with return code: " + status ) )
                        );
            }
        }
        catch ( IOException e )
        {
            reply = false;
            if ( log.isErrorEnabled() )
            {
                log.error
                        (
                                banner + "failed: " + e.getMessage()
                        );
            }
        }
        return
                reply;
    }

    /**
     * lists all log in/out events stored by service;
     *
     * @return list of events grouped by username;
     */
    public List< LogInOutResponse > getEventsList()
            throws
                URISyntaxException
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
            throws
                URISyntaxException
    {
        URIBuilder builder = new URIBuilder( this.remote );
        if ( StringUtils.hasText( application ) )
        {
            builder.addParameter( "application", application.trim() );
        }
        if ( after != null )
        {
            builder.addParameter( "after", LogInOut.convertDate( after ) );
        }
        if ( before != null )
        {
            builder.addParameter( "before", LogInOut.convertDate( before ) );
        }
        URI     uri     = builder.build();

        String banner  = "listing events - end point '" + uri.toString() + "' - ";
        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            banner + "begin"
                    );
        }

        List< LogInOutResponse > list = null;
        try
        {
            InputStream stream = super.getEventsList( uri );
            if ( stream != null )
            {
                list = this.mapper.readValue
                        (
                                stream,
                                new TypeReference< List< LogInOutResponse > >()
                                {
                                }
                        );
            }
            if ( log.isInfoEnabled() )
            {
                log.info
                        (
                                banner + "events got: " + ( ( list != null ) ? list.size() : "empty" )
                        );
            }
        }
        catch ( IOException e )
        {
            if ( log.isErrorEnabled() )
            {
                log.error
                        (
                                banner + e.getMessage()
                        );
            }
        }
        return
                list;
    }

}
