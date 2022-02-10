package net.alemas.oss.tools.eventscollector.repositories.postgres;


import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import net.alemas.oss.tools.eventscollector.io.timing.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;
import net.alemas.oss.tools.eventscollector.repositories.TimerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


/**
 * Repository to store timer events in PostgreSQL database;
 *
 * Created by MASCHERPA on 30/09/2021.
 */
@Repository
@ConditionalOnProperty
        (
                value           = "server.repository.type",
                havingValue     = "postgres"
        )
public class TimerRepositoryPostgres
        extends     RepositoryPostgres< TimingEvent, TimingResponse >
        implements  TimerRepository
{
    /* --- constants --- */
    private static final String     TABLE   = "timers";

    /* --- constructors --- */
    @Autowired
    public TimerRepositoryPostgres
        (
                ConnectionPostgres    postgres
        )
    {
        super( postgres );
    }

    /* --- interface methods --- */
    @Override
    public void add( TimingEvent event ) throws IllegalArgumentException
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
                                                                + TABLE + "( application, event_id, date, elapsed )"
                                                                + " values( $1, $2, $3, $4 )"
                                                        )
                                                .bind( "$1", event.getApplication() )
                                                .bind( "$2", event.getId() )
                                                .bind( "$3", event.getWhen() )
                                                .bind( "$4", event.getElapsed() )
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
        String                  sql     = "select "
                                        +       '*'
                                        + " from "
                                        +       TABLE
                ;
        CreateWhereStatement    where   =
                new CreateWhereStatement
                        (
                                application,
                                null,
                                after,
                                before
                        );
        sql = where.appendWhereStatement( sql );

        Statement statement = connection.createStatement( sql );
        where.bindParameters( statement );
        return
                statement;
    }

    @Override
    protected TimingEvent mapEvent( Row row )
    {
        return
                new TimingEvent
                        (
                                row.get( "application", String.class ),
                                row.get( "event_id",    String.class ),
                                row.get( "date",        LocalDateTime.class ),
                                row.get( "elapsed",     Double.class )
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
        String                  sql     = "select "
                                        +       "application" + ", "
                                        +       "event_id" + ", "
                                        +       "count( elapsed ) as count" + ", "
                                        +       "avg( elapsed ) as avg" + ", "
                                        +       "min( elapsed ) as min" + ", "
                                        +       "max( elapsed ) as max"
                                        + " from "
                                        +       TABLE
                                        ;
        CreateWhereStatement    where   =
                new CreateWhereStatement
                        (
                                application,
                                null,
                                after,
                                before
                        );
        sql = where.appendWhereStatement( sql );
        sql += " group by "
            +       "application, event_id"
            ;

        Statement statement = connection.createStatement( sql );
        where.bindParameters( statement );
        return
                statement;
    }

    @Override
    protected TimingResponse mapResponse( Row row )
    {
        return
                new TimingResponse
                        (
                                row.get( "application", String.class ),
                                row.get( "event_id",    String.class ),
                                row.get( "count",       Long.class ),
                                row.get( "avg",         Double.class ),
                                row.get( "min",         Double.class ),
                                row.get( "max",         Double.class )
                        );
    }

}
