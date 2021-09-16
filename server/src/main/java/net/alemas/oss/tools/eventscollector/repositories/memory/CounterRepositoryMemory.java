package net.alemas.oss.tools.eventscollector.repositories.memory;


import net.alemas.oss.tools.eventscollector.io.counter.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.counter.CounterResponse;
import net.alemas.oss.tools.eventscollector.io.linking.PairApplicationIdUsernameId;
import net.alemas.oss.tools.eventscollector.repositories.CounterRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

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
public class CounterRepositoryMemory implements CounterRepository
{
    /* --- properties -- */
    private List< CounterEvent > repository  = new ArrayList<>();

    /* --- handlers --- */
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

    public Flux< CounterResponse > groupById()
    {
        return
                Flux
                        .fromIterable( this.repository )
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
                                                                                    new CounterResponse
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

}
