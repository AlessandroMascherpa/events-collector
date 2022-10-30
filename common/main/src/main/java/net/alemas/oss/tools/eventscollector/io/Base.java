package net.alemas.oss.tools.eventscollector.io;


import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Class to define common properties for all events.
 *
 * Created by MASCHERPA on 14/10/2022.
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

    @Schema
            (
                    required    = true,
                    description = "The unique event identifier.",
                    nullable    = false
            )
    private String              id;

    /* --- constructors --- */
    public Base
            (
                    String  system,
                    String  id
            )
    {
        this.application    = system;
        this.id             = id;
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

    public String getId()
    {
        return this.id;
    }
    public void setId( String id )
    {
        this.id = id;
    }

    /* --- object checking --- */
    @Override
    public void isWellFormed() throws NotWellFormed
    {
        if ( ! StringUtils.hasText( this.application ) )
        {
            throw
                    new NotWellFormed
                            (
                                    this.getClass().getSimpleName(),
                                    "application"
                            );
        }
        if ( ! StringUtils.hasText( this.id ) )
        {
            throw
                    new NotWellFormed
                            (
                                    this.getClass().getSimpleName(),
                                    "id"
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

        Base that = ( o instanceof Base ) ? ( (Base) o ) : null;
        return
                (
                        ( that != null )
                        &&
                        areEqual( that.application, that.application )
                        &&
                        areEqual( that.id, that.id )
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
        int result = ( this.application != null ) ? this.application.hashCode() : 0;
        result = 31 * result + ( ( this.id != null ) ? this.id.hashCode() : 0 );
        return
                result
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
