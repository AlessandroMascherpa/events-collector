package net.alemas.oss.tools.eventscollector.io;


import net.alemas.oss.tools.eventscollector.io.linking.PairApplicationIdUsernameId;
import net.alemas.oss.tools.eventscollector.io.timing.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;

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
    protected static final String                                                       TIMER_ID_1 = "timer-1";
    protected static final String                                                       TIMER_ID_2 = "timer-2";
    protected static final String                                                       TIMER_ID_3 = "timer-3";

    protected static final List< TimingEvent >                                          events;
    protected static final List< TimingEvent >                                          eventsDot;
    protected static final List< TimingResponse >                                       responses;
    protected static final List< TimingResponse >                                       responsesDot;
    protected static final Map< PairApplicationIdUsernameId, List< EventElapsed > >     scenarioDot    = new HashMap<>();
    protected static final Map< PairApplicationIdUsernameId, List< EventElapsed > >     scenarioOrg    = new HashMap<>();
    static
    {
        scenarioDot.put
                (
                        PairApplicationIdUsernameId.build( APP_DOT, TIMER_ID_1 ),
                        Collections.singletonList
                                (
                                        EventElapsed.build( 2021, 5, 21, 22, 5, 3, 1.0D )
                                )
                );

        scenarioDot.put
                (
                        PairApplicationIdUsernameId.build( APP_DOT, TIMER_ID_2 ),
                        Arrays.asList
                                (
                                        EventElapsed.build( 2021, 5, 21, 22, 15, 20, 2.0D ),
                                        EventElapsed.build( 2021, 5, 21, 22, 55, 22, 4.0D )
                                )
                );

        scenarioOrg.put
                (
                        PairApplicationIdUsernameId.build( APP_ORG, TIMER_ID_3 ),
                        Arrays.asList
                                (
                                        EventElapsed.build( 2021, 5, 22, 21, 34, 45, 1.0D ),
                                        EventElapsed.build( 2021, 5, 22, 21, 52, 36, 2.0D ),
                                        EventElapsed.build( 2021, 5, 22, 21,  2, 12, 3.0D )
                                )
                );

        eventsDot       = prepareEvents( scenarioDot );
        responsesDot    = prepareResponses( scenarioDot );

        events          = prepareEvents( scenarioOrg );
        responses       = prepareResponses( scenarioOrg );

        events.addAll( eventsDot );
        responses.addAll( responsesDot );
    }
    private static List< TimingEvent >  prepareEvents( Map< PairApplicationIdUsernameId, List< EventElapsed > > scenario )
    {
        List< TimingEvent >  events = new ArrayList<>();
        for ( Map.Entry< PairApplicationIdUsernameId, List< EventElapsed > > listEntry : scenario.entrySet() )
        {
            PairApplicationIdUsernameId pair = listEntry.getKey();
            for ( EventElapsed time : listEntry.getValue() )
            {
                events.add
                        (
                                new TimingEvent
                                        (
                                                pair.getApplication(),
                                                pair.getId(),
                                                time.when,
                                                time.elapsed
                                        )
                        );
            }
        }
        return
                events;
    }
    private static List< TimingResponse >  prepareResponses( Map< PairApplicationIdUsernameId, List< EventElapsed > > scenario )
    {
        List< TimingResponse >  responses = new ArrayList<>();
        for ( Map.Entry< PairApplicationIdUsernameId, List< EventElapsed > > listEntry : scenario.entrySet() )
        {
            PairApplicationIdUsernameId pair    = listEntry.getKey();
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
                                            pair.getApplication(),
                                            pair.getId(),
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
        failures.add( new TimingEvent( null,    "invalid", null,                              0.0D ) );
        failures.add( new TimingEvent( null,    "",        asDate( 2021, 5, 14, 23, 24, 12 ), 0.0D ) );
        failures.add( new TimingEvent( null,    null,      asDate( 2021, 5, 14, 23, 24, 36 ), 0.0D ) );
        failures.add( new TimingEvent( null,    null,      null,                              0.0D ) );

        failures.add( new TimingEvent( "",      "invalid", null,                              0.0D ) );
        failures.add( new TimingEvent( "",      "",        asDate( 2021, 5, 14, 23, 24, 12 ), 0.0D ) );
        failures.add( new TimingEvent( "",      null,      asDate( 2021, 5, 14, 23, 24, 36 ), 0.0D ) );
        failures.add( new TimingEvent( "",      null,      null,                              0.0D ) );

        failures.add( new TimingEvent( "empty", "invalid", null,                              0.0D ) );
        failures.add( new TimingEvent( "empty", "",        asDate( 2021, 5, 14, 23, 24, 12 ), 0.0D ) );
        failures.add( new TimingEvent( "empty", null,      asDate( 2021, 5, 14, 23, 24, 36 ), 0.0D ) );
        failures.add( new TimingEvent( "empty", null,      null,                              0.0D ) );

    }

}
