package net.alemas.oss.tools.eventscollector.repositories.postgres;


import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutEvent;
import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutResponse;
import net.alemas.oss.tools.eventscollector.repositories.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


/**
 * Repository to store log in/out events in PostgreSQL database;
 *
 * Created by MASCHERPA on 30/09/2021.
 */
@Repository
@ConditionalOnProperty
        (
                value           = "server.repository.type",
                havingValue     = "postgres"
        )
public class LoginRepositoryPostgres
        extends     RepositoryPostgres< LogInOutEvent, LogInOutResponse >
        implements  LoginRepository
{
    /* --- constants --- */
    private static final String     TABLE   = "loginout";

    /* --- constructors --- */
    @Autowired
    public LoginRepositoryPostgres
        (
                ConnectionPostgres    postgres
        )
    {
        super( postgres );
    }

    /* --- interface methods --- */
    @Override
    public void add( LogInOutEvent event ) throws IllegalArgumentException
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
                                                                + TABLE + "( application, username, session_id, date, \"in\" )"
                                                                + " values( $1, $2, $3, $4, $5 )"
                                                        )
                                                .bind( "$1", event.getApplication() )
                                                .bind( "$2", event.getUsername() )
                                                .bind( "$3", event.getSession() )
                                                .bind( "$4", event.getWhen() )
                                                .bind( "$5", event.isIn() )
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
    protected LogInOutEvent mapEvent( Row row )
    {
        return
                new LogInOutEvent
                        (
                                row.get( "application", String.class ),
                                row.get( "username", String.class ),
                                row.get( "session_id", String.class ),
                                row.get( "date", LocalDateTime.class ),
                                Boolean.TRUE.equals( row.get( "in", Boolean.class ) )
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
                                        +       "loggedin.application" + ", "
                                        +       "loggedin.username" + ", "
                                        +       "loggedin.date" + " as " + "date_in" + ", "
                                        +       "loggedout.date" + " as " + "date_out"
                                        + " from "
                                        +   '('
                                        +       "select "
                                        +           '*'
                                        +       " from "
                                        +           TABLE
                                        +       " where "
                                        +           "\"in\" = true"
                                        +   ") " + "loggedin"
                                        + " left join "
                                        +   '('
                                        +       "select "
                                        +           '*'
                                        +       " from "
                                        +           TABLE
                                        +       " where "
                                        +           "\"in\" = false"
                                        +   ") " + "loggedout"
                                        +   " on "
                                        +       "loggedin.application = loggedout.application"
                                        +   " and "
                                        +       "loggedin.username = loggedout.username"
                                        +   " and "
                                        +       "loggedin.session_id = loggedout.session_id"
                                        +   " and "
                                        +       "loggedin.date < loggedout.date"
                                        +   " order by "
                                        +       "application" + ',' + "username" + ',' + "date_in"
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
    protected LogInOutResponse mapResponse( Row row )
    {
        return
                new LogInOutResponse
                        (
                                row.get( "application", String.class ),
                                row.get( "username", String.class ),
                                row.get( "date_in", LocalDateTime.class ),
                                row.get( "date_out", LocalDateTime.class )
                        );
    }

}
