package net.alemas.oss.tools.eventscollector.repositories;


import net.alemas.oss.tools.eventscollector.io.in.EventElapsed;
import net.alemas.oss.tools.eventscollector.io.out.EventsStatistics;


/**
 * The repository stores timing events.
 *
 * Created by MASCHERPA on 14/09/2021.
 */
public interface TimerRepository extends Repository< EventElapsed, EventsStatistics >
{
}
