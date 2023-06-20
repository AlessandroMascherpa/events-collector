package net.alemas.oss.tools.eventscollector.repositories.memory;


import net.alemas.oss.tools.eventscollector.io.counter.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.out.EventsCounter;
import net.alemas.oss.tools.eventscollector.io.linking.PairApplicationIdUsernameId;
import net.alemas.oss.tools.eventscollector.repositories.CounterRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The repository stores single events.
 *
 * Created by MASCHERPA on 06/05/2021.
 */
@Repository
@ConditionalOnProperty
        (
                value           = "server.repository.type",
                havingValue     = "memory",
                matchIfMissing  = true
        )
public class CounterRepositoryMemory
        implements
            CounterRepository, RepositoryMemoryConstants
{
    /* --- properties -- */
    private List< CounterEvent > repository  = new ArrayList<>();

    /* --- handlers --- */
    @Override
    public void add( CounterEvent event ) throws IllegalArgumentException
    {
        if ( event == null )
        {
            throw
                    new IllegalArgumentException( "Event not defined." );
        }
        event.isWellFormed();

        if ( this.repository.size() > REPOSITORY_SIZE_LIMIT )
        {
            this.repository
                    = this.repository
                    .stream()
                    .limit( REPOSITORY_SIZE_MIN )
                    .collect
                            (
                                    Collectors.toList()
                            )
            ;
        }
        this.repository.add( event );
    }


    @Override
    public Flux< CounterEvent > list
            (
                    String          application,
                    LocalDateTime   after,
                    LocalDateTime   before
            )
    {
        return
                Flux
                        .fromIterable( this.repository )
                        .filter
                                (
                                        event ->
                                                this.filterByApplicationAndDate
                                                        (
                                                                event,
                                                                application,
                                                                after,
                                                                before
                                                        )
                                )
                ;
    }


    @Override
    public Flux< EventsCounter > groupById
            (
                    String          application,
                    LocalDateTime   after,
                    LocalDateTime   before
            )
    {
        return
                Flux
                        .fromIterable( this.repository )
                        .filter
                                (
                                        event ->
                                                this.filterByApplicationAndDate
                                                        (
                                                                event,
                                                                application,
                                                                after,
                                                                before
                                                        )
                                )
                        .groupBy
                                (
                                        event ->
                                                PairApplicationIdUsernameId.build
                                                        (
                                                                event.getApplication(),
                                                                event.getId()
                                                        )
                                )
                        .flatMap
                                (
                                        grouped ->
                                                grouped
                                                        .collectList()
                                                        .map
                                                                (
                                                                        eventsByKey ->
                                                                        {
                                                                            PairApplicationIdUsernameId key = grouped.key();
                                                                            return
                                                                                    new EventsCounter
                                                                                            (
                                                                                                    key.getApplication(),
                                                                                                    key.getId(),
                                                                                                    eventsByKey.size()
                                                                                            );
                                                                        }
                                                                )
                                )
                ;
    }

    protected boolean filterByApplicationAndDate
            (
                    CounterEvent    event,
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
