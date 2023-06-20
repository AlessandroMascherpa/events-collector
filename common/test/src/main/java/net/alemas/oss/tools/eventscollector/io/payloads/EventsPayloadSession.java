package net.alemas.oss.tools.eventscollector.io.payloads;


import net.alemas.oss.tools.eventscollector.io.in.EventSession;
import net.alemas.oss.tools.eventscollector.io.out.EventsStatistics;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;


public class EventsPayloadSession extends EventsPayloadBase< PayloadSession, EventSession, EventsStatistics >
{

    /* --- constructors --- */
    protected EventsPayloadSession( String application, String id )
    {
        super( application, id );
    }

    public static EventsPayloadSession build( String application, String id )
    {
        return
                new EventsPayloadSession( application, id );
    }

    @Override
    public EventsPayloadSession add( PayloadSession payloadElapsed )
    {
        return
                (EventsPayloadSession) super.add( payloadElapsed );
    }

    /* --- abstract methods implementation --- */
    @Override
    protected EventSession createEvent
            (
                    String          application,
                    String          id,
                    PayloadSession  payload
            )
    {
        return
                new EventSession
                        (
                                application,
                                id,
                                payload.getWhen(),
                                payload.getSession()
                        );
    }

    @Override
    protected List< EventsStatistics > prepareResponses
            (
                    String                  application,
                    String                  id,
                    List< PayloadSession >  payloads
            )
    {
/* --delete--
        Map< String, SessionTimeBounds >    sessions    = new HashMap<>();
        for ( PayloadSession payload : payloads )
        {
            String              session = payload.getSession();
            SessionTimeBounds   bounds  = sessions.get( session );
            if ( bounds == null )
            {
                sessions.put
                        (
                                session,
                                new SessionTimeBounds( payload.getWhen() )
                        );
            }
            else
            {
                bounds.add( payload.getWhen() );
            }
        }
   --delete-- */
        DoubleSummaryStatistics stats   =
                payloads
                        .stream()
                        .map
                                (
                                        session ->
                                                new SessionDateTime( session.getSession(), session.getWhen() )
                                )
                        .collect
                                (
                                        Collectors.toMap
                                                (
                                                        sessionWhen -> sessionWhen.session,
                                                        sessionWhen -> new SessionTimeBounds
                                                                (
                                                                        sessionWhen.when
                                                                ),
                                                        ( sessionWhen, sessionWhen2 ) -> sessionWhen.add( sessionWhen2.first )
                                                )
                                )
                        .values()
                        .stream()
                        .mapToDouble( SessionTimeBounds::getElapsed )
                        .summaryStatistics()
                ;
        return
                List.of
                        (
                                new EventsStatistics
                                        (
                                                application,
                                                id,
                                                stats.getCount(),
                                                stats.getAverage(),
                                                stats.getMin(),
                                                stats.getMax()
                                        )
                        )
                ;
    }

    /* --- internal classes --- */
    private static final class SessionDateTime
    {
        private final String        session;
        private final LocalDateTime when;

        public SessionDateTime( String session, LocalDateTime when )
        {
            this.session    = session;
            this.when       = when;
        }
    }
    private static final class SessionTimeBounds
    {
        private LocalDateTime   first;
        private LocalDateTime   last;

        private SessionTimeBounds( LocalDateTime time )
        {
            this.first = time;
        }
        private SessionTimeBounds add( LocalDateTime time )
        {
            if ( this.first == null )
            {
                this.first = time;
            }
            else if ( this.first.compareTo( time ) > 0 )
            {
                LocalDateTime tmp = this.first;
                this.first = time;
                if ( this.last == null )
                {
                    this.last = tmp;
                }
                else if ( this.last.compareTo( tmp ) < 0 )
                {
                    this.last = tmp;
                }
            }
            else
            {
                if ( this.last == null )
                {
                    this.last = time;
                }
                else if ( this.last.compareTo( time ) < 0 )
                {
                    this.last = time;
                }
            }
            return
                    this;
        }
        private long getElapsed()
        {
            return
                    ( ( this.first != null ) && ( this.last != null ) )
                            ? Duration.between( this.first, this.last ).toMillis()
                            : 0L
                    ;
        }
    }

}
