package net.alemas.oss.tools.eventscollector.repositories.postgres;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import net.alemas.oss.tools.eventscollector.io.counter.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.counter.CounterResponse;
import net.alemas.oss.tools.eventscollector.repositories.CounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


/**
 * Repository to store single events in PostgreSQL database;
 *
 * Created by MASCHERPA on 30/09/2021.
 */
@Repository
@ConditionalOnProperty
        (
                value           = "server.repository.type",
                havingValue     = "postgres"
        )
public class CounterRepositoryPostgres
        extends     RepositoryPostgres< CounterEvent, CounterResponse >
        implements  CounterRepository
{
    /* --- constants --- */
    private static final String     TABLE   = "counters";

    /* --- constructors --- */
    @Autowired
    public CounterRepositoryPostgres
        (
                ConnectionPostgres    postgres
        )
    {
        super( postgres );
    }

    /* --- interface methods --- */
    @Override
    public void add( CounterEvent event ) throws IllegalArgumentException
    {
        if ( event == null )
        {
            throw
                    new IllegalArgumentException( "Event not defined." );
        }
        event.isWellFormed();

        Mono
                .from
                        (
                                this.connection.getConnection()
                        )
                .flatMapMany
                        (
                                connection ->
                                        connection
                                                .createStatement
                                                        (
                                                                "insert into "
                                                                + TABLE + "( application, event_id, date )"
                                                                + " values( $1, $2, $3 )"
                                                        )
                                                .bind( "$1", event.getApplication() )
                                                .bind( "$2", event.getId() )
                                                .bind( "$3", event.getWhen() )
                                                .execute()
                        )
                .flatMap( Result::getRowsUpdated )
                .log()
                .subscribe
                        (
                                updated ->
                                        log.info( "inserted {} row with: {}", updated, event.toString() )
                        )
                ;
    }

    /* --- abstract methods definition --- */
    @Override
    protected Statement createStatementForList
        (
                Connection      connection,
                String          application,
                LocalDateTime   after,
                LocalDateTime   before
        )
    {
        CreateWhereStatement    where   =
                new CreateWhereStatement
                        (
                                application,
                                null,
                                after,
                                before
                        )
                ;
        String                  sql     = "select "
                                        +       '*'
                                        + " from "
                                        +       TABLE
                                        + where.appendWhereStatement()
                ;

        Statement statement = connection.createStatement( sql );
        where.bindParameters( statement );
        return
                statement;
    }

    @Override
    protected CounterEvent mapEvent( Row row )
    {
        return
                new CounterEvent
                        (
                                row.get( "application", String.class ),
                                row.get( "event_id",    String.class ),
                                row.get( "date",        LocalDateTime.class )
                        );
    }


    @Override
    protected Statement createStatementForGroupBy
            (
                    Connection      connection,
                    String          application,
                    LocalDateTime   after,
                    LocalDateTime   before
            )
    {
        CreateWhereStatement    where   =
                new CreateWhereStatement
                        (
                                application,
                                null,
                                after,
                                before
                        )
                ;
        String                  sql     = "select "
                                        +       "application" + ", "
                                        +       "event_id" + ", "
                                        +       "count( date ) as count"
                                        + " from "
                                        +       TABLE
                                        + where.appendWhereStatement()
                                        + " group by "
                                        +       "application, event_id"
                ;

        Statement statement = connection.createStatement( sql );
        where.bindParameters( statement );
        return
                statement;
    }

    @Override
    protected CounterResponse mapResponse( Row row )
    {
        return
                new CounterResponse
                        (
                                row.get( "application", String.class ),
                                row.get( "event_id",    String.class ),
                                row.get( "count",       Long.class )
                        );
    }

}
