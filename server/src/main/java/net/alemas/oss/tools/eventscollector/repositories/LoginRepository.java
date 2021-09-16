package net.alemas.oss.tools.eventscollector.repositories;


import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutEvent;
import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


/**
 * Manages the logs in and out events.
 *
 * Created by MASCHERPA on 14/09/2021.
 */
public interface LoginRepository extends Repository
{
    /* --- constants --- */
    /**
     * oldest event since now;
     */
    long  REPOSITORY_WINDOW_DAYS      = 15;

    /* --- handlers --- */
    /**
     * inserts in the repository a new event;
     *
     * @param event    the event to add;
     * @throws IllegalArgumentException if the event was not well formed;
     */
    void add( LogInOutEvent event ) throws IllegalArgumentException;

    Mono< LogInOutEvent > getByRowNumber( int row ) throws ArrayIndexOutOfBoundsException;

    /**
     * list of events filtered by formal parameters;
     *
     * @param application    events belonging to;
     * @param after          events after the given date;
     * @param before         events before the given date;
     * @return the list of requested events;
     */
    Flux< LogInOutResponse >  list
            (
                    String        application,
                    LocalDateTime after,
                    LocalDateTime before
            );

}
