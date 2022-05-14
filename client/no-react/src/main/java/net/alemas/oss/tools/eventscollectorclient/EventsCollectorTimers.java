package net.alemas.oss.tools.eventscollectorclient;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.alemas.oss.tools.eventscollector.configuration.EndpointsPaths;
import net.alemas.oss.tools.eventscollector.io.timing.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Client library to post timing events and enquiry the service.
 *
 * Created by MASCHERPA on 27/05/2021.
 */
public class EventsCollectorTimers extends EventsCollector
{
    /* --- constants --- */
    /**
     * service path;
     */
    private static final String PATH_BASE   = EndpointsPaths.FULL_PATH_TIMER_EVENT;

    /* --- constructors --- */
    /**
     * Creates new connector to remote service for timing events;
     *
     * @param remote    the host name and port of the remote service;
     *
     * @throws URISyntaxException       if the URI is not correct;
     * @throws IllegalArgumentException if the URI was not defined;
     */
    public EventsCollectorTimers( URI remote )
            throws
                URISyntaxException, IllegalArgumentException
    {
        super( remote, PATH_BASE );
    }
    /**
     * Creates new connector to remote service for timing events;
     *
     * @param remote            the host name and port of the remote service;
     * @param timeoutConnection connection timeout (in milliseconds);
     * @param timeoutRequest    request timeout (in milliseconds);
     * @param timeoutSocket     socket timeout (in milliseconds);
     *
     * @throws IllegalArgumentException if the URI was not defined;
     */
    public EventsCollectorTimers
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
        boolean reply;
        String  date    = TimingEvent.convertDate( when );
        String  banner  = "posting timing event - end point '" + this.remote.toString() + "' - event: application: '" + application + "', id: '" + id + "', when: " + date + " - ";
        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            banner + "begin"
                    );
        }
        try
        {
            /* --- prepare body --- */
            TimingEvent event = new TimingEvent( application, id, when, elapsed );

			/* --- execute call and check reply --- */
            int status = super.postEvent( event );

            reply = ( status == HttpStatus.SC_CREATED );

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
     * lists all timing events grouped by its identifier;
     *
     * @return list of events grouped by identifier;
     */
    public List< TimingResponse > getEventsList()
            throws
                URISyntaxException
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
            throws
                URISyntaxException
    {
        URI     uri     = super.buildUri( application, after, before );
        String  banner  = "getting list of counting events - end point '" + uri.toString() + "' - ";
        if ( log.isDebugEnabled() )
        {
            log.debug( banner + "begin" );
        }

        List< TimingResponse > list = null;
        try
        {
            InputStream stream = super.getEventsList( uri );
            if ( stream != null )
            {
                ObjectMapper mapper = new ObjectMapper();

                list = mapper.readValue
                        (
                                stream,
                                new TypeReference< List< TimingResponse > >()
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
        if ( log.isDebugEnabled() )
        {
            log.debug( banner + "end" );
        }
        return
                list;
    }

}
