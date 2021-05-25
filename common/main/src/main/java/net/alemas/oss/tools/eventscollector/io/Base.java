package net.alemas.oss.tools.eventscollector.io;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Class to define common properties;
 *
 * Created by MASCHERPA on 04/05/2021.
 */
public abstract class Base
{
    /* --- constants --- */
    /**
     * exchanged date/time format;
     */
    public static final String   DATE_TIME_PATTERN   = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    /**
     * text string used when a date is not defined;
     */
    private static final String  DATE_UNDEFINED     = "undefined";

    /* --- static utility methods --- */
    private static final DateTimeFormatter formatter	= DateTimeFormatter.ofPattern( Base.DATE_TIME_PATTERN );

    /**
     * writes a date/time according the format handled by this class;
     *
     * @param when    the instance to write;
     * @return the date/time formatted in stringtext ;
     */
    public static String convertDate( LocalDateTime when )
    {
        return
                ( when != null )
                        ? when.format( formatter )
                        : DATE_UNDEFINED
                ;
    }

}
