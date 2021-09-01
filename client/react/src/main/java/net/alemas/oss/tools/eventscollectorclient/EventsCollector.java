package net.alemas.oss.tools.eventscollectorclient;


import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import net.alemas.oss.tools.eventscollectorclient.configuration.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;


/**
 * Library to post events to statistics collector service and
 * to query it.
 *
 * Created by MASCHERPA on 16/02/2021.
 */
public abstract class EventsCollector
{
    /* --- logging --- */
    final protected static Logger log = LoggerFactory.getLogger( EventsCollector.class );

    /* --- properties --- */
    /**
     * remote service URL;
     */
    protected UriComponentsBuilder  url;

    /**
     * reactive client connector;
     */
    protected WebClient.Builder     client;

    /* --- constructors --- */
    /**
     * Creates new connector to remote service;
     *
     * @param remote    the host name and port of the remote service;
     * @param basePath  remote service base path;
     *
     * @throws MalformedURLException    if the URI is not correct;
     * @throws IllegalArgumentException if the URI was not defined;
     */
    public EventsCollector
        (
                URI     remote,
                String  basePath
        )
        throws
                MalformedURLException, IllegalArgumentException
    {
        this( remote, basePath, 1000, 10, 500, 3, 3 );
    }
    /**
     * Creates new connector to remote service;
     *
     * @param remote            the host name and port of the remote service;
     * @param basePath          remote service base path;
     * @param connections       maximum connections per connection pool;
     * @param acquire           maximum acquire time per connection (in seconds);
     * @param timeOutConnection connection time out (in milliseconds);
     * @param timeOutRead       read time out (in seconds);
     * @param timeOutWrite      write time out (in seconds);
     *
     * @throws MalformedURLException    if the URI is not correct;
     * @throws IllegalArgumentException if the URI was not defined;
     */
    public EventsCollector
        (
                URI     remote,
                String  basePath,
                int     connections,
                int     acquire,
                int     timeOutConnection,
                int     timeOutRead,
                int     timeOutWrite
        )
        throws
                MalformedURLException, IllegalArgumentException
    {
        if ( remote == null )
        {
            throw
                    new IllegalArgumentException( "Remote service address not defined." );
        }

        ClientProperties  properties   = new ClientProperties();

        this.url =
                UriComponentsBuilder
                        .fromUri( remote )
                        .path( properties.getBasePath() )
                        .path( basePath )
        ;

        ConnectionProvider  provider =
                ConnectionProvider
                        .builder( properties.getProviderName() )
                        .maxConnections( connections )
                        .pendingAcquireTimeout
                                (
                                        Duration.ofSeconds( acquire )
                                )
                        .build()
                ;

        HttpClient          http =
                HttpClient
                        .create( provider )
                        .option( ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOutConnection )
                        .doOnConnected
                                (
                                        connection ->
                                        {
                                            connection.addHandlerLast
                                                    (
                                                            new ReadTimeoutHandler( timeOutRead )
                                                    );
                                            connection.addHandlerLast
                                                    (
                                                            new WriteTimeoutHandler( timeOutWrite )
                                                    );
                                        }
                                )
                ;

         this.client =
                 WebClient
                         .builder()
                         .clientConnector
                                 (
                                         new ReactorClientHttpConnector( http )
                                 )
         ;
    }

}
