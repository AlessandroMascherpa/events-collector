package net.alemas.oss.tools.eventscollectorclient;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.alemas.oss.tools.eventscollector.io.timing.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;
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
    private static final String PATH_BASE   = "/events/timings/";

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
			/* --- prepare form --- */
            List< NameValuePair > encoded		= new ArrayList<>();
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
                                            "id",
                                            id
                                    )
                    );
            encoded.add
                    (
                            new BasicNameValuePair
                                    (
                                            "when",
                                            TimingEvent.convertDate( when )
                                    )
                    );
            encoded.add
                    (
                            new BasicNameValuePair
                                    (
                                            "elapsed",
                                            String.valueOf( elapsed )
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
     * lists all timing events grouped by its identifier;
     *
     * @return list of events grouped by identifier;
     */
    public List< TimingResponse > getEventsList()
    {
        String banner  = "getting list of timing events - end point '" + this.remote.toString() + "' - ";
        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            banner + "begin"
                    );
        }

        List< TimingResponse > list = null;
        try
        {
            InputStream stream = super.getEventsList( this.remote );
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
