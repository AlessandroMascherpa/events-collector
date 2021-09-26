package net.alemas.oss.tools.eventscollector.repositories;


import net.alemas.oss.tools.eventscollector.io.Base;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;


/**
 * Common interface for all repositories and constants definitions;
 *
 * Created by MASCHERPA on 14/09/2021.
 */
public interface Repository< Event extends Base, Response extends Base >
{
    /* --- handlers --- */
    /**
     * inserts in the repository a new event;
     *
     * @param event    the event to add;
     * @throws IllegalArgumentException if the event was not well formed;
     */
    void add( Event event ) throws IllegalArgumentException;

    /**
     * lists all stored events filtered by formal parameters;
     * All filters are in "and";
     *
     * @param application    filter by application; it can be null;
     * @param after          events after the given date;
     * @param before         events before the given date;
     * @return the filtered list of stored events;
     */
    Flux< Event >  list
            (
                    String        application,
                    LocalDateTime after,
                    LocalDateTime before
            )
        ;

    /**
     * lists all events stored grouped by application and identifier;
     * All filters are in "and";
     *
     * @param application    filter by application; it can be null;
     * @param after          events after the given date;
     * @param before         events before the given date;
     * @return the filtered list of stored events;
     */
    Flux< Response > groupById
            (
                    String        application,
                    LocalDateTime after,
                    LocalDateTime before
            )
        ;

}
