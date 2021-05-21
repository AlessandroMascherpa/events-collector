package eventscollectorclient;


import net.alemas.oss.tools.eventscollector.EventsCollectorApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.SocketUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * The class starts'n'stops the remote service and it gives the service context;
 *
 * Created by MASCHERPA on 14/05/2021.
 */
public class ServerContext
{
    /* --- logging --- */
    final private static Logger log = LoggerFactory.getLogger( ServerContext.class );

    /* --- properties --- */
    /**
     * remote service context;
     */
    private final ConfigurableApplicationContext  context;

    /**
     * the remote service: host name and port;
     */
    private final URI                             service;

    /* --- constructors --- */
    public ServerContext() throws URISyntaxException
    {
        int port = setRandomPort( 9000, 9900 );

        this.context = new SpringApplicationBuilder( EventsCollectorApplication.class ).run();
        this.service = new URI( "http", null, "localhost", port, null, null, null );
    }
    public static int setRandomPort( int minPort, int maxPort )
    {
        int port;
        try
        {
            String userDefinedPort = System.getProperty( "server.port", System.getenv( "SERVER_PORT" ) );
            if ( StringUtils.hasText( userDefinedPort ) )
            {
                port = Integer.valueOf( userDefinedPort );
                log.info( "Server port already set in environment as {}.", port) ;
            }
            else
            {
                port = SocketUtils.findAvailableTcpPort( minPort, maxPort );
                System.setProperty( "server.port", String.valueOf( port ) );
                log.info( "Server port set to {}.", port) ;
            }
        }
        catch ( IllegalStateException e )
        {
            port = 8090;
            log.warn( "No port available in range {}-{}. Default embedded server configuration will be used.", minPort, maxPort );
        }
        return
                port;
    }

    /* --- handlers --- */
    public void close()
    {
        this.context.close();
    }

    /* --- getters --- */
    public URI getService()
    {
        return this.service;
    }

}
