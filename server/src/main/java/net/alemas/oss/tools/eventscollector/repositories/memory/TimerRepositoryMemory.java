package net.alemas.oss.tools.eventscollector.repositories.memory;


import net.alemas.oss.tools.eventscollector.io.linking.PairApplicationIdUsernameId;
import net.alemas.oss.tools.eventscollector.io.timing.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;
import net.alemas.oss.tools.eventscollector.repositories.TimerRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The repository stores timing events.
 *
 * Created by MASCHERPA on 26/05/2021.
 */
@Repository
@ConditionalOnProperty
        (
                value           = "server.repository.type",
                havingValue     = "memory",
                matchIfMissing  = true
        )
public class TimerRepositoryMemory
        implements
            TimerRepository, RepositoryMemoryConstants
{
    /* --- properties -- */
    private List< TimingEvent > repository  = new ArrayList<>();

    /* --- handlers --- */
    @Override
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


    @Override
    public Flux< TimingEvent > list
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
    public Flux< TimingResponse > groupById
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

    protected boolean filterByApplicationAndDate
            (
                    TimingEvent     event,
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
