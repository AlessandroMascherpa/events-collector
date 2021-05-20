package net.alemas.oss.tools.eventscollectorclient;


import net.alemas.oss.tools.eventscollectorclient.configuration.ClientProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;

import java.net.URI;
import java.net.URISyntaxException;


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
    protected final RequestConfig       config;

    protected final URI                 remote;

	/* --- constructor --- */
    public EventsCollector
        (
                URI     remote,
                String  path
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
                        .setConnectTimeout( 500 )
                        .setConnectionRequestTimeout( 500 )
                        .setSocketTimeout( 500 )
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
    }

}
