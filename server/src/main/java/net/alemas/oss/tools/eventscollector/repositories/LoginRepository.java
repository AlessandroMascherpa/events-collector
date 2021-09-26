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
public interface LoginRepository extends Repository< LogInOutEvent, LogInOutResponse >
{
    /* --- constants --- */
    /**
     * oldest event since now;
     */
    long  REPOSITORY_WINDOW_DAYS      = 15;

    /* --- handlers --- */

    @Deprecated
    Mono< LogInOutEvent > getByRowNumber( int row ) throws ArrayIndexOutOfBoundsException;

}
