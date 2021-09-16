package net.alemas.oss.tools.eventscollector.repositories;


import net.alemas.oss.tools.eventscollector.io.counter.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.counter.CounterResponse;
import reactor.core.publisher.Flux;


/**
 * The repository stores single events.
 *
 * Created by MASCHERPA on 14/09/2021.
 */
public interface CounterRepository extends Repository
{
    /* --- handlers --- */
    /**
     * inserts in the repository a new event;
     *
     * @param event    the event to add;
     * @throws IllegalArgumentException if the event was not well formed;
     */
    void add( CounterEvent event ) throws IllegalArgumentException;

    /**
     * lists all events stored grouped by their identifier;
     *
     * @return the list of stored events;
     */
    Flux< CounterResponse > groupById();

}
