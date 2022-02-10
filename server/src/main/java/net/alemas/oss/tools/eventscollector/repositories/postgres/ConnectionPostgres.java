package net.alemas.oss.tools.eventscollector.repositories.postgres;


import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * Service class to get the connection to Postgres database.
 *
 * The connection URL: r2dbc:postgresql://<username>:<password>@<host>:5432/events-collector
 *
 * Created by MASCHERPA on 05/01/2022.
 */
@Service
public class ConnectionPostgres
{
    /* --- logging --- */
    final protected static Logger log = LoggerFactory.getLogger( ConnectionPostgres.class );

    /* --- properties --- */
    /**
     * the connection URL;
     */
    private final String                        url;

    /**
     * the database connection;
     */
    private Publisher<? extends Connection >    connection;

    /* --- constructor --- */
    public ConnectionPostgres
        (
                @Value( "${server.repository.url}" ) String repositoryUrl
        )
    {
        this.url = repositoryUrl;
    }

    /* --- getters --- */
    public Publisher< ? extends Connection > getConnection()
    {
        if ( this.connection == null )
        {
            if ( log.isInfoEnabled() )
            {
                log.info( "attempt to create database connection using URL: '{}'", this.url );
            }
            ConnectionFactory connectionFactory =
                    ConnectionFactories
                            .get( this.url )
                    ;
            this.connection =
                    connectionFactory.create()
            ;
            if ( log.isInfoEnabled() )
            {
                log.info( "connection created: {}", this.connection );
            }
        }
        return
                this.connection;
    }

}
