package net.alemas.oss.tools.eventscollectorclient;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.alemas.oss.tools.eventscollector.io.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.CounterResponse;
import net.alemas.oss.tools.eventscollector.io.LogInOut;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



/**
 * Client library to post single events and enquiry the service.
 *
 * Created by MASCHERPA on 20/05/2021.
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
     * @throws URISyntaxException       if the URI is not correct;
     * @throws IllegalArgumentException if the URI was not defined;
     */
    public EventsCollectorCounters( URI remote )
            throws
                URISyntaxException, IllegalArgumentException
    {
        super( remote, PATH_BASE );
    }
    /**
     * Creates new connector to remote service for single events;
     *
     * @param remote            the host name and port of the remote service;
     * @param timeoutConnection connection timeout (in milliseconds);
     * @param timeoutRequest    request timeout (in milliseconds);
     * @param timeoutSocket     socket timeout (in milliseconds);
     *
     * @throws IllegalArgumentException if the URI was not defined;
     */
    public EventsCollectorCounters
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
        boolean reply;
        String  date    = LogInOut.convertDate( when );
        String  banner  = "posting single event - end point '" + this.remote.toString() + "' - event: id: '" + id + "', when: " + date + " - ";
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
                                            "id",
                                            id
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
     * lists all single events grouped by its identifier;
     *
     * @return list of events grouped by identifier;
     */
    public List< CounterResponse > getEventsList()
    {
        String banner  = "getting list of counting events - end point '" + this.remote.toString() + "' - ";
        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            banner + "begin"
                    );
        }

        List< CounterResponse > list = null;
        try
        {
            InputStream stream = super.getEventsList( this.remote );
            if ( stream != null )
            {
                ObjectMapper mapper = new ObjectMapper();

                list = mapper.readValue
                        (
                                stream,
                                new TypeReference< List< CounterResponse > >()
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
            list = null;
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