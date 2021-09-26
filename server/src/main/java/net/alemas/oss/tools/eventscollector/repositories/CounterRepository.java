package net.alemas.oss.tools.eventscollector.repositories;


import net.alemas.oss.tools.eventscollector.io.counter.CounterEvent;
import net.alemas.oss.tools.eventscollector.io.counter.CounterResponse;
import reactor.core.publisher.Flux;


/**
 * The repository stores single events.
 *
 * Created by MASCHERPA on 14/09/2021.
 */
public interface CounterRepository extends Repository< CounterEvent, CounterResponse >
{
}
