package net.alemas.oss.tools.eventscollector.repositories.memory;


import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutEvent;
import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutResponse;
import net.alemas.oss.tools.eventscollector.repositories.LoginRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Manages the logs in and out events.
 *
 * Created by MASCHERPA on 04/02/2021.
 */
@Repository
@ConditionalOnProperty
        (
                value           = "server.repository.type",
                havingValue     = "memory",
                matchIfMissing  = true
        )
public class LoginRepositoryMemory implements LoginRepository
{
    /* --- properties -- */
    private List< LogInOutEvent > repository  = new ArrayList<>();

    /* --- handlers --- */
    public void add( LogInOutEvent event ) throws IllegalArgumentException
    {
        if ( event == null )
        {
            throw
                    new IllegalArgumentException( "Log In/Out event not defined." );
        }
        event.isWellFormed();

        if ( this.repository.size() > REPOSITORY_SIZE_LIMIT )
        {
            LocalDateTime now = LocalDateTime.now();
            this.repository
                    = this.repository
                    .stream()
                    .filter
                            (
                                    item ->
                                            ChronoUnit.DAYS.between( now, item.getWhen() ) < REPOSITORY_WINDOW_DAYS
                            )
                    .limit( REPOSITORY_SIZE_MIN )
                    .collect( Collectors.toList() )
            ;
        }
        this.repository.add( event );
    }

    public Mono< LogInOutEvent >  getByRowNumber( int row ) throws ArrayIndexOutOfBoundsException
    {
        int size = this.repository.size();
        if ( ( row < 0 ) || ( row >= size ) )
        {
            throw
                    new ArrayIndexOutOfBoundsException
                            (
                                    ( row < 0 )
                                    ? "Attempt to access row by using negative index."
                                    : String.format( "Attempt to access row at index: %d, but there are only %d items.", row, size )
                            )
                    ;
        }
        return
                Mono.just( this.repository.get( row ) );
    }


    static private final long          SECONDS_IN_OUT_WINDOW = ( 20 * 60 );

    public Flux< LogInOutResponse >  list
            (
                    String        application,
                    LocalDateTime after,
                    LocalDateTime before
            )
    {

        List< LogInOutEvent >    outs
                = this.repository
                .stream()
                .filter
                        ( t ->
                                        (
                                                ( ! t.isIn() )
                                                &&
                                                this.filterByApplicationAndDate( t, application, after, before )
                                        )
                        )
                .collect( Collectors.toList() )
                ;

        return
            Flux.create
                    (
                            ( FluxSink< LogInOutResponse > sink ) ->
                            {
                                this.repository
                                        .stream()
                                        .filter
                                                ( t ->
                                                                (
                                                                        t.isIn()
                                                                        &&
                                                                        this.filterByApplicationAndDate( t, application, after, before )
                                                                )
                                                )
                                        .forEach
                                                (
                                                        t ->
                                                        {
                                                            LogInOutEvent out
                                                                    = outs
                                                                    .stream()
                                                                    .filter
                                                                            (
                                                                                    o ->
                                                                                            (
                                                                                                    t.getUsername().equals( o.getUsername() )
                                                                                                    &&
                                                                                                    t.getApplication().equals( o.getApplication() )
                                                                                                    &&
                                                                                                    t.getWhen().isBefore( o.getWhen() )
                                                                                                    &&
                                                                                                    ( ChronoUnit.SECONDS.between( o.getWhen(), t.getWhen() ) < SECONDS_IN_OUT_WINDOW )
                                                                                            )
                                                                            )
                                                                    .findFirst()
                                                                    .orElse( null )
                                                                    ;
                                                            sink.next
                                                                    (
                                                                            new LogInOutResponse
                                                                                    (
                                                                                            t.getApplication(),
                                                                                            t.getUsername(),
                                                                                            t.getWhen(),
                                                                                            ( out != null ) ? out.getWhen() : null
                                                                                    )
                                                                    )
                                                            ;
                                                            outs.remove( out );
                                                        }
                                            )
                                ;
                                sink.complete()
                                ;
                            }
                    )
                    ;
    }
    private boolean filterByApplicationAndDate
            (
                    LogInOutEvent   event,
                    String          application,
                    LocalDateTime   after,
                    LocalDateTime   before
            )
    {
        LocalDateTime when = event.getWhen();
        return
                ( ( application == null ) || ( event.getApplication().equals( application ) ) )
                &&
                ( ( after == null )  || ( when.compareTo( after ) >= 0 ) )
                &&
                ( ( before == null ) || ( when.compareTo( before ) <= 0 ) )
                ;
    }

}
