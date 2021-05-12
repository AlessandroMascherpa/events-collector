package net.alemas.oss.tools.eventscollector;


import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * Class with common payloads for testing.
 *
 * Created by MASCHERPA on 11/05/2021.
 */
public abstract class EventsBase
{

    /* --- payload methods --- */
    protected static LocalDateTime asDate( int year, int month, int day, int hours, int minutes, int seconds )
    {
        return
                LocalDateTime.of( year, month,day, hours, minutes, seconds, 0 )
                ;
    }

}
