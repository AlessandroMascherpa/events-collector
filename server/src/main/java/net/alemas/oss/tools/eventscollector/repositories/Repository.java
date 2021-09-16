package net.alemas.oss.tools.eventscollector.repositories;



/**
 * Common interface for all repositories as constants definitions;
 *
 * Created by MASCHERPA on 14/09/2021.
 */
public interface Repository
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
