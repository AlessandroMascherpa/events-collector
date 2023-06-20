package net.alemas.oss.tools.eventscollector.repositories.postgres;


import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import net.alemas.oss.tools.eventscollector.io.in.EventElapsed;
import net.alemas.oss.tools.eventscollector.io.out.EventsStatistics;
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
        extends     RepositoryPostgres< EventElapsed, EventsStatistics >
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
    public void add( EventElapsed event ) throws IllegalArgumentException
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
    protected EventElapsed mapEvent( Row row )
    {
        Double elapsed = row.get( "elapsed", Double.class );
        return
                new EventElapsed
                        (
                                row.get( "application", String.class ),
                                row.get( "event_id",    String.class ),
                                row.get( "date",        LocalDateTime.class ),
                                ( elapsed != null ) ? elapsed : 0.0D
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
                                        +       "count( elapsed ) as count" + ", "
                                        +       "avg( elapsed ) as avg" + ", "
                                        +       "min( elapsed ) as min" + ", "
                                        +       "max( elapsed ) as max"
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
    protected EventsStatistics mapResponse( Row row )
    {
        Long count = row.get( "count", Long.class );
        Double avg = row.get( "avg",   Double.class );
        Double min = row.get( "min",   Double.class );
        Double max = row.get( "max",   Double.class );
        return
                new EventsStatistics
                        (
                                row.get( "application", String.class ),
                                row.get( "event_id",    String.class ),
                                ( count != null ) ? count : 0,
                                ( avg   != null ) ? avg   : 0.0D,
                                ( min   != null ) ? min   : 0.0D,
                                ( max   != null ) ? max   : 0.0D
                        );
    }

}
