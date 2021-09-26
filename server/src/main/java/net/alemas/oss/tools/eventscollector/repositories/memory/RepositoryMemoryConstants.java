package net.alemas.oss.tools.eventscollector.repositories.memory;


import net.alemas.oss.tools.eventscollector.io.counter.CounterEvent;

import java.time.LocalDateTime;

/**
 * Common interface for all memory repositories;
 *
 * Created by MASCHERPA on 23/09/2021.
 */
public interface RepositoryMemoryConstants
{
    /* --- constants --- */
    /**
     * maximum events in the repository;
     */
    int   REPOSITORY_SIZE_LIMIT       = 2000;
    /**
     * minimum events in the repository;
     */
    int   REPOSITORY_SIZE_MIN         = 1500;

}
