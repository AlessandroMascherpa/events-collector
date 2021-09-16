package net.alemas.oss.tools.eventscollector.repositories.memory;


import net.alemas.oss.tools.eventscollector.io.linking.PairApplicationIdUsernameId;
import net.alemas.oss.tools.eventscollector.io.timing.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The repository stores timing events.
 *
 * Created by MASCHERPA on 26/05/2021.
 */
@Repository
public class TimerRepositoryMemory
{
    /* --- constants --- */
    /**
     * maximum events in the repository;
     */
    private static final int   REPOSITORY_SIZE_LIMIT       = 2000;
    /**
     * minimum events in the repository;
     */
    private static final int   REPOSITORY_SIZE_MIN         = 1500;


    /* --- properties -- */
    private List< TimingEvent > repository  = new ArrayList<>();

    /* --- handlers --- */
    public void add( TimingEvent event ) throws IllegalArgumentException
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

    public Flux< TimingResponse > groupById()
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
                                                        .collect
                                                                (
                                                                        Collectors.summarizingDouble
                                                                                (
                                                                                        TimingEvent::getElapsed
                                                                                )
                                                                )
                                                        .map
                                                                (
                                                                        eventsByKey ->
                                                                        {
                                                                            PairApplicationIdUsernameId key = grouped.key();
                                                                            return
                                                                                    new TimingResponse
                                                                                            (
                                                                                                    key.getApplication(),
                                                                                                    key.getId(),
                                                                                                    eventsByKey.getCount(),
                                                                                                    eventsByKey.getAverage(),
                                                                                                    eventsByKey.getMin(),
                                                                                                    eventsByKey.getMax()
                                                                                            );
                                                                        }
                                                                )
                                )
                ;
    }

}
