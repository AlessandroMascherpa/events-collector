package net.alemas.oss.tools.eventscollector.io;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Class to define common properties;
 *
 * Created by MASCHERPA on 04/05/2021.
 */
public abstract class Base implements WellFormed
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

    /* --- properties --- */
    @Schema
            (
                    required    = true,
                    description = "The application name where the event occurred.",
                    nullable    = false
            )
    private String             application;

    /* --- constructors --- */
    public Base
        (
                String  system
        )
    {
        this.application    = system;
    }

    /* --- getters'n'setters --- */
    public String getApplication()
    {
        return this.application;
    }
    public void setApplication( String name )
    {
        this.application = name;
    }

    /* --- object checking --- */
    @Override
    public void isWellFormed() throws NotWellFormed
    {
        if ( ( this.application == null ) || "".equals( this.application ) )
        {
            throw
                    new NotWellFormed
                            (
                                    String.format( "Class: '%s', property '%s' was not defined.", this.getClass().getSimpleName(), "application" )
                            );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( ( o == null ) || ( this.getClass() != o.getClass() ) )
        {
            return false;
        }

        Base that = ( o instanceof Base ) ? ( (Base) o ) : null;
        return
                (
                        ( that != null )
                        &&
                        areEqual( that.application, that.application )
                )
                ;
    }
    protected static boolean areEqual( Object thisObj, Object thatObj )
    {
        return
                (
                        ( ( thisObj != null ) && ( thisObj.equals( thatObj ) ) )
                        ||
                        ( ( thisObj == null ) && ( thatObj == null ) )
                )
                ;
    }

    @Override
    public int hashCode()
    {
        return
                ( this.application != null ) ? this.application.hashCode() : 0
                ;
    }

    /* --- debug related --- */
    public static String safeLogString( String msg )
    {
        return
                ( msg != null )
                ? msg.replaceAll( "[\r\n]", "<cr/lf>" )
                : "<null>"
                ;
    }
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
