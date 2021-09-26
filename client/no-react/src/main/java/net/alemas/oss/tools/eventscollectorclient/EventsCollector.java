package net.alemas.oss.tools.eventscollectorclient;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.alemas.oss.tools.eventscollector.io.Base;
import net.alemas.oss.tools.eventscollectorclient.configuration.ClientProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;


/**
 * Library to post events to statistics collector service and
 * to query it.
 *
 * Created by MASCHERPA on 19/05/2021.
 */
public abstract class EventsCollector
{
    /* --- logging --- */
    protected static final Log log = LogFactory.getLog( EventsCollector.class );

    /* --- properties --- */
    private final   RequestConfig       config;

    protected final URI                 remote;

    private final   ObjectMapper        mapper;

	/* --- constructor --- */
    public EventsCollector
        (
                URI     remote,
                String  path
        )
            throws
                URISyntaxException, IllegalArgumentException
    {
        this( remote, path, 500, 500, 500 );
    }
    public EventsCollector
        (
                URI     remote,
                String  path,
                int     timeoutConnection,
                int     timeoutRequest,
                int     timeoutSocket
        )
            throws
                URISyntaxException, IllegalArgumentException
    {
        if ( remote == null )
        {
            throw
                    new IllegalArgumentException( "Remote service address not defined." );
        }

        this.config =
                RequestConfig
                        .custom()
                        .setConnectTimeout( timeoutConnection )
                        .setConnectionRequestTimeout( timeoutRequest )
                        .setSocketTimeout( timeoutSocket )
                        .build()
        ;

        ClientProperties properties = new ClientProperties();
        this.remote =
                new URI
                        (
                                remote.getScheme(),
                                null,
                                remote.getHost(),
                                remote.getPort(),
                                properties.getBasePath() + path,
                                null,
                                null
                        )
        ;

        this.mapper =
                JsonMapper
                    .builder()
                    .addModule( new JavaTimeModule() )
                    .build()
                ;
    }

    /* --- connectors --- */
    protected int postEvent
        (
                Base  event
        )
            throws
                IOException
    {
        /* --- prepare body --- */
        String body = this.mapper.writeValueAsString( event );

        /* --- prepare call --- */
        HttpEntityEnclosingRequestBase request	= new HttpPost
                (
                        this.remote
                );
        request.setHeader
                (
                        HttpHeaders.CONTENT_TYPE,
                        ContentType.APPLICATION_JSON.getMimeType()
                );
        request.setEntity
                (
                        new StringEntity( body, ContentType.APPLICATION_JSON )
                );
        request.setConfig
                (
                        this.config
                );

        /* --- execute call and check reply --- */
        CloseableHttpResponse response	=
                HttpClients
                        .createDefault()
                        .execute( request )
                ;
        return
                response
                        .getStatusLine()
                        .getStatusCode()
                ;
    }

    protected URI buildUri
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
            builder.addParameter( "after", Base.convertDate( after ) );
        }
        if ( before != null )
        {
            builder.addParameter( "before", Base.convertDate( before ) );
        }
        return
                builder.build()
                ;
    }

    protected InputStream getEventsList
            (
                    URI  uri
            )
            throws
                IOException
    {
        /* --- prepare call --- */
        HttpGet request = new HttpGet
                (
                        uri
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

        InputStream body;
        boolean     reply = ( status == HttpStatus.SC_OK );
        if ( reply )
        {
            body = response
                    .getEntity()
                    .getContent()
                    ;
        }
        else
        {
            throw
                    new IOException
                            (
                                    "failed with return code: " + status
                            )
                    ;
        }
        return
                body;
    }

}
