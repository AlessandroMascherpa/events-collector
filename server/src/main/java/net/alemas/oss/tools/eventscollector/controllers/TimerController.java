package net.alemas.oss.tools.eventscollector.controllers;


import io.swagger.annotations.*;
import net.alemas.oss.tools.eventscollector.configuration.EndpointsPaths;
import net.alemas.oss.tools.eventscollector.configuration.ServerConfiguration;
import net.alemas.oss.tools.eventscollector.exporters.SpreadsheetTimers;
import net.alemas.oss.tools.eventscollector.io.Base;
import net.alemas.oss.tools.eventscollector.io.timing.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;
import net.alemas.oss.tools.eventscollector.repositories.TimerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.LocalDateTime;


/**
 * API controller to collect timing events;
 *
 * Created by MASCHERPA on 26/05/2021.
 */
@RestController
@RequestMapping( "${server.base-path}" + EndpointsPaths.REQUEST_PATH_TIMER_BASE )
@Api
        (
                description = "API controller to collect timing events and to provide statistics about them."
        )
public class TimerController
{
    /* --- logging --- */
    final private static Logger log = LoggerFactory.getLogger( TimerController.class );

    /* --- properties --- */
    /*
     * add here any data repository that supports non-blocking reactive streams;
     */
    private final TimerRepository     repository;

    /**
     * server configuration properties;
     */
    private final ServerConfiguration properties;

    /**
     * spread sheet exporter;
     */
    private final SpreadsheetTimers   exporter;


    /* --- constructors --- */
    @Autowired
    public TimerController
        (
                TimerRepository     repository,
                ServerConfiguration properties,
                SpreadsheetTimers   exporter
        )
    {
        this.repository = repository;
        this.properties = properties;
        this.exporter   = exporter;
    }

    /* --- end-points --- */
    @ApiOperation
            (
                    value = "To store a timer event.",
                    code = 201
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse( code = 201, message = "Event stored" ),
                                    @ApiResponse( code = 404, message = "If client posted an invalid event." )
                            }
            )
    @RequestMapping
            (
                    value		= EndpointsPaths.REQUEST_PATH_TIMER_EVENT,
                    method		= RequestMethod.POST,
                    consumes	= MediaType.APPLICATION_JSON_VALUE
            )
    @ResponseStatus( HttpStatus.CREATED )
    private void addCounter
        (
                @RequestBody TimingEvent event
        )
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "timer event - event: '{}' - begin", event.toString() );
        }
        try
        {
            this.repository.add( event );
        }
        catch ( Exception e )
        {
            if ( log.isWarnEnabled() )
            {
                log.warn( "timer event - failure", e );
            }
            throw
                    new ResponseStatusException
                            (
                                    HttpStatus.BAD_REQUEST,
                                    e.getMessage(),
                                    e
                            )
                    ;
        }
        if ( log.isInfoEnabled() )
        {
            log.info( "timer event - end" );
        }
    }


    @ApiOperation
            (
                    value = "List stored timers as JSON objects array.",
                    code = 200
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse( code = 200, message = "Events listed." )
                            }
            )
    @RequestMapping
            (
                    value       = EndpointsPaths.REQUEST_PATH_TIMER_EVENT,
                    method      = RequestMethod.GET,
                    produces    = MediaType.APPLICATION_JSON_VALUE
            )
    @ResponseStatus( HttpStatus.OK )
    private Flux< TimingResponse > getEventsGroupedById
            (
                    @ApiParam
                            (
                                    value           = "To list events linked to an application name.",
                                    required        = false,
                                    allowEmptyValue = false
                            )
                    @RequestParam( required = false )
                    String        application,

                    @ApiParam
                            (
                                    value           = "To list events after the given date/time; the date/time pattern is: '" + Base.DATE_TIME_PATTERN + "'.",
                                    format          = Base.DATE_TIME_PATTERN,
                                    required        = false,
                                    allowEmptyValue = false
                            )
                    @RequestParam( required = false )
                    @DateTimeFormat( pattern = Base.DATE_TIME_PATTERN )
                    LocalDateTime after,

                    @ApiParam
                            (
                                    value           = "To list events before the given date/time; the date/time pattern is: '" + Base.DATE_TIME_PATTERN + "'.",
                                    format          = Base.DATE_TIME_PATTERN,
                                    required        = false,
                                    allowEmptyValue = false
                            )
                    @RequestParam( required = false )
                    @DateTimeFormat( pattern = Base.DATE_TIME_PATTERN )
                    LocalDateTime before
            )
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "returning list of events as json objects array." );
        }
        return
                this.repository
                        .groupById( application, after, before )
                ;
    }


    @ApiOperation
            (
                    value = "List stored timers as spreadsheet file.",
                    code = 200
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse( code = 200, message = "Events listed." )
                            }
            )
    @RequestMapping
            (
                    value       = EndpointsPaths.REQUEST_PATH_TIMER_EVENT_XLSX,
                    method      = RequestMethod.GET,
                    produces    = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
    private ResponseEntity< Mono< Resource > > getEventsGroupedByIdAsSpreadsheet
            (
                    @ApiParam
                            (
                                    value           = "To list events linked to an application name.",
                                    required        = false,
                                    allowEmptyValue = false
                            )
                    @RequestParam( required = false )
                    String        application,

                    @ApiParam
                            (
                                    value           = "To list events after the given date/time; the date/time pattern is: '" + Base.DATE_TIME_PATTERN + "'.",
                                    format          = Base.DATE_TIME_PATTERN,
                                    required        = false,
                                    allowEmptyValue = false
                            )
                    @RequestParam( required = false )
                    @DateTimeFormat( pattern = Base.DATE_TIME_PATTERN )
                    LocalDateTime after,

                    @ApiParam
                            (
                                    value           = "To list events before the given date/time; the date/time pattern is: '" + Base.DATE_TIME_PATTERN + "'.",
                                    format          = Base.DATE_TIME_PATTERN,
                                    required        = false,
                                    allowEmptyValue = false
                            )
                    @RequestParam( required = false )
                    @DateTimeFormat( pattern = Base.DATE_TIME_PATTERN )
                    LocalDateTime before
            )
            throws
                IOException
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "returning list of timers as spreadsheet" );
        }

        String name =
                this.properties
                        .getFileNameSpreadsheet()
                ;

        ContentDisposition attachment =
                ContentDisposition
                        .attachment()
                        .filename( name + ".timers.xlsx" )
                        .build()
                ;
        return
                ResponseEntity
                        .ok()
                        .header( HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" )
                        .header( HttpHeaders.CONTENT_DISPOSITION, attachment.toString() )
                        .cacheControl( CacheControl.noCache() )
                        .body
                                (
                                        Mono.fromCallable
                                                (
                                                        () ->
                                                                this.exporter
                                                                        .export
                                                                                (
                                                                                        this.repository
                                                                                                .groupById( application, after, before )
                                                                                )
                                                )
                                            .subscribeOn
                                                    (
                                                            Schedulers.boundedElastic()
                                                    )
                                )
                ;
    }

}
