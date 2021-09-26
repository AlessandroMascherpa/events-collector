package net.alemas.oss.tools.eventscollector.repositories;


import net.alemas.oss.tools.eventscollector.io.timing.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;
import reactor.core.publisher.Flux;


/**
 * The repository stores timing events.
 *
 * Created by MASCHERPA on 14/09/2021.
 */
public interface TimerRepository extends Repository< TimingEvent, TimingResponse >
{
}
