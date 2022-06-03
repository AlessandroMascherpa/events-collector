package net.alemas.oss.tools.eventscollector.repositories.postgres;


import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import net.alemas.oss.tools.eventscollector.io.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * Common class for PostgreSQL repository.
 *
 * Created by MASCHERPA on 30/09/2021.
 */
public abstract class RepositoryPostgres< Event extends Base, Response extends Base >
{
    /* --- logging --- */
    final protected static Logger log = LoggerFactory.getLogger( RepositoryPostgres.class );

    /* --- properties --- */
    protected ConnectionPostgres    connection;

    /* --- constructors --- */
    public RepositoryPostgres
        (
                ConnectionPostgres    postgres
        )
    {
        this.connection = postgres;
    }

    /* --- interface methods --- */
    public Flux< Event > list
        (
                String          application,
                LocalDateTime   after,
                LocalDateTime   before
        )
    {
        return
                Mono
                        .from
                                (
                                        this.connection.getConnection()
                                )
                        .flatMapMany
                                (
                                        connection ->
                                                this.createStatementForList
                                                        (
                                                                connection,
                                                                application,
                                                                after,
                                                                before
                                                        )
                                                    .execute()
                                )
                        .flatMap
                                (
                                        result ->
                                                result.map
                                                        (
                                                                ( row, rowMetadata ) ->
                                                                        this.mapEvent( row )
                                                        )
                                )
                        .log()
                ;
    }

    public Flux< Response > groupById
        (
                String        application,
                LocalDateTime after,
                LocalDateTime before
        )
    {
        return
                Mono
                        .from
                                (
                                        this.connection.getConnection()
                                )
                        .flatMapMany
                                (
                                        connection ->
                                                this
                                                        .createStatementForGroupBy
                                                                (
                                                                        connection,
                                                                        application,
                                                                        after,
                                                                        before
                                                                )
                                                        .execute()
                                )
                        .flatMap
                                (
                                        result ->
                                                result.map
                                                        (
                                                                ( row, rowMetadata ) ->
                                                                        this.mapResponse( row )
                                                        )
                                )
                        .log()
                ;
    }

    /* --- abstract methods --- */
    protected abstract Statement createStatementForList
            (
                    Connection      connection,
                    String          application,
                    LocalDateTime   after,
                    LocalDateTime   before
            );

    protected abstract Event mapEvent( Row row );

    protected abstract Statement createStatementForGroupBy
            (
                    Connection      connection,
                    String          application,
                    LocalDateTime   after,
                    LocalDateTime   before
            );

    protected abstract Response mapResponse( Row row );


    /* --- utility class --- */
    protected static class CreateWhereStatement
    {
        /* --- properties --- */
        private       String                  where   = "";
        private final Map< String, Object >   params  = new HashMap<>( 4 );

        /* --- constructors --- */
        protected CreateWhereStatement
            (
                    String          application,
                    String          username,
                    LocalDateTime   after,
                    LocalDateTime   before
            )
        {
            String  and = "";
            if ( StringUtils.hasText( application ) )
            {
                this.where  += and + "application = $1";
                and         =  " and ";
                this.params.put( "$1", application );
            }
            if ( StringUtils.hasText( username ) )
            {
                this.where  += and + "username = $2";
                and         =  " and ";
                this.params.put( "$2", username );
            }
            if ( after != null )
            {
                this.where  += and + "date >= $3";
                and         =  " and ";
                this.params.put( "$3", after );
            }
            if ( before != null )
            {
                this.where  += and + "date <= $4";
                and         =  " and ";
                this.params.put( "$4", before );
            }
        }

        /* --- getters --- */
        protected String appendWhereStatement()
        {
            return
                    ( this.where.length() > 0 )
                    ? ( " where " + this.where )
                    : ""
                    ;
        }

        protected String appendAndStatement()
        {
            return
                    ( this.where.length() > 0 )
                    ? ( " and " + this.where )
                    : ""
                    ;
        }

        protected void bindParameters( Statement statement )
        {
            for ( Map.Entry< String, Object > entry : this.params.entrySet() )
            {
                statement.bind
                        (
                                entry.getKey(), entry.getValue()
                        );
            }
        }

    }

}
