package net.alemas.oss.tools.eventscollectorclient;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.alemas.oss.tools.eventscollector.io.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.CounterResponse;
import net.alemas.oss.tools.eventscollector.io.LogInOut;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



/**
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
    public EventsCollectorCounters( URI remote )
            throws
                URISyntaxException, IllegalArgumentException
    {
        super( remote, PATH_BASE );
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

			/* --- prepare call --- */
            HttpEntityEnclosingRequestBase request	= new HttpPost
                    (
                            this.remote
                    );
            request.setHeader
                    (
                            HttpHeaders.CONTENT_TYPE,
                            ContentType.APPLICATION_FORM_URLENCODED.getMimeType()
                    );
            request.setEntity
                    (
                            new UrlEncodedFormEntity( encoded )
                    );
            request.setConfig
                    (
                            this.config
                    );

			/* --- execute call and check reply --- */
            CloseableHttpResponse   response	=
                    HttpClients
                            .createDefault()
                            .execute( request )
                    ;
            int						status		=
                    response
                            .getStatusLine()
                            .getStatusCode()
                    ;

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
        List< CounterResponse > list    = null;
        String                  banner  = "getting list of counting events - end point '" + this.remote.toString() + "' - ";
        if ( log.isDebugEnabled() )
        {
            log.debug
                    (
                            banner + "begin"
                    );
        }
        try
        {
			/* --- prepare call --- */
            HttpGet request = new HttpGet
                    (
                            this.remote
                    );
            request.setHeader
                    (
                            HttpHeaders.CONTENT_TYPE,
                            ContentType.APPLICATION_FORM_URLENCODED.getMimeType()
                    );
            request.setConfig
                    (
                            this.config
                    );

			/* --- execute call and check reply --- */
            CloseableHttpResponse   response	=
                    HttpClients
                            .createDefault()
                            .execute( request )
                    ;
            int						status		=
                    response
                            .getStatusLine()
                            .getStatusCode()
                    ;

            boolean reply = ( status == HttpStatus.SC_NO_CONTENT );
            if ( reply )
            {
                ObjectMapper mapper = new ObjectMapper();

                list = mapper.readValue
                        (
                                response
                                        .getEntity()
                                        .getContent(),
                                new TypeReference< List< CounterResponse > >()
                                {
                                }
                        );
            }

            if ( log.isInfoEnabled() )
            {
                log.info
                        (
                                banner +
                                (
                                        reply
                                                ? ( "events got: " + list.size() )
                                                : ( "failed with return code: " + status )
                                )
                        );
            }
        }
        catch ( IOException e )
        {
            if ( log.isErrorEnabled() )
            {
                log.error
                        (
                                banner + "failed: " + e.getMessage()
                        );
            }
        }
        return
                list;
    }

}
