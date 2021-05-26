package net.alemas.oss.tools.eventscollector;


import net.alemas.oss.tools.eventscollector.io.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.TimingResponse;

import java.time.LocalDateTime;
import java.util.*;


/**
 * Class with common test data for timer events
 *
 * Created by MASCHERPA on 26/05/2021.
 */
public class EventsTiming extends EventsBase< TimingResponse >
{
    /* --- payload values --- */
    protected static final String                               TIMER_ID_1 = "timer-1";
    protected static final String                               TIMER_ID_2 = "timer-2";
    protected static final String                               TIMER_ID_3 = "timer-3";

    protected static final List< TimingEvent >                  events;
    protected static final List< TimingResponse >               responses;
    protected static final Map< String, List< EventElapsed > >  scenario    = new HashMap<>();
    static
    {
        scenario.put
                (
                        TIMER_ID_1,
                        Collections.singletonList
                                (
                                        EventElapsed.build( 2021, 5, 21, 22, 5, 3, 1.0D )
                                )
                );

        scenario.put
                (
                        TIMER_ID_2,
                        Arrays.asList
                                (
                                        EventElapsed.build( 2021, 5, 21, 22, 15, 20, 2.0D ),
                                        EventElapsed.build( 2021, 5, 21, 22, 55, 22, 4.0D )
                                )
                );

        scenario.put
                (
                        TIMER_ID_3,
                        Arrays.asList
                                (
                                        EventElapsed.build( 2021, 5, 22, 21, 34, 45, 1.0D ),
                                        EventElapsed.build( 2021, 5, 22, 21, 52, 36, 2.0D ),
                                        EventElapsed.build( 2021, 5, 22, 21,  2, 12, 3.0D )
                                )
                );

        events      = prepareEvents( scenario );
        responses   = prepareResponses( scenario );
    }
    private static List< TimingEvent >  prepareEvents( Map< String, List< EventElapsed > > scenario )
    {
        List< TimingEvent >  events = new ArrayList<>();
        for ( Map.Entry< String, List< EventElapsed > > listEntry : scenario.entrySet() )
        {
            for ( EventElapsed time : listEntry.getValue() )
            {
                events.add
                        (
                                new TimingEvent( listEntry.getKey(), time.when, time.elapsed )
                        );
            }
        }
        return
                events;
    }
    private static List< TimingResponse >  prepareResponses( Map< String, List< EventElapsed > > scenario )
    {
        List< TimingResponse >  responses = new ArrayList<>();
        for ( Map.Entry< String, List< EventElapsed > > listEntry : scenario.entrySet() )
        {
            DoubleSummaryStatistics stats   =
                    listEntry
                        .getValue()
                        .stream()
                        .mapToDouble( value -> value.elapsed )
                        .summaryStatistics()
                    ;

            responses.add
                    (
                            new TimingResponse
                                    (
                                            listEntry.getKey(),
                                            stats.getCount(),
                                            stats.getAverage(),
                                            stats.getMin(),
                                            stats.getMax()
                                    )
                    );
        }
        return
                responses;
    }

    /* --- internal classes --- */
    private static final class EventElapsed
    {
        private final LocalDateTime   when;
        private final double          elapsed;

        private static EventElapsed build( int year, int month, int day, int hours, int minutes, int seconds, double elapsed )
        {
            return
                    new EventElapsed( year, month, day, hours, minutes, seconds, elapsed );
        }
        private EventElapsed( int year, int month, int day, int hours, int minutes, int seconds, double elapsed )
        {
            this.elapsed    = elapsed;
            this.when       = asDate( year, month, day, hours, minutes, seconds );
        }
    }


    /* --- invalid payload values --- */
    protected static final List< TimingEvent >                 failures    = new ArrayList<>();
    static
    {
        failures.add( new TimingEvent( "invalid", null,                              0.0D ) );
        failures.add( new TimingEvent( "",        asDate( 2021, 5, 14, 23, 24, 12 ), 0.0D ) );
        failures.add( new TimingEvent( null,      asDate( 2021, 5, 14, 23, 24, 36 ), 0.0D ) );
        failures.add( new TimingEvent( null,      null,                              0.0D ) );
    }

}
