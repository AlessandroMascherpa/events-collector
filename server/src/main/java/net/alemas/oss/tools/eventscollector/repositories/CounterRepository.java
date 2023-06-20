package net.alemas.oss.tools.eventscollector.repositories;


import net.alemas.oss.tools.eventscollector.io.counter.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.out.EventsCounter;


/**
 * The repository stores single events.
 *
 * Created by MASCHERPA on 14/09/2021.
 */
public interface CounterRepository extends Repository< CounterEvent, EventsCounter >
{
}
