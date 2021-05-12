package net.alemas.oss.tools.eventscollector.repositories;


import net.alemas.oss.tools.eventscollector.io.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.CounterResponse;
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
public class CounterRepository
{
    /* --- constants --- */
    /**
     * maximum events in the repository;
     */
    private final int   REPOSITORY_SIZE_LIMIT       = 2000;
    /**
     * minimum events in the repository;
     */
    private final int   REPOSITORY_SIZE_MIN         = 1500;


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
                        .groupBy( CounterEvent::getId )
                        .flatMap
                                (
                                        grouped ->
                                                grouped
                                                        .collectList()
                                                        .map
                                                                (
                                                                        eventsByKey -> new CounterResponse
                                                                                (
                                                                                        grouped.key(),
                                                                                        eventsByKey.size()
                                                                                )
                                                                )
                                )
                ;
    }

}
