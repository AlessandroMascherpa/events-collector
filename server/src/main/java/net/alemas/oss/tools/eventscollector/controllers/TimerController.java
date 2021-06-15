package net.alemas.oss.tools.eventscollector.controllers;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.alemas.oss.tools.eventscollector.configuration.ServerConfiguration;
import net.alemas.oss.tools.eventscollector.exporters.SpreadsheetTimers;
import net.alemas.oss.tools.eventscollector.io.timing.TimingEvent;
import net.alemas.oss.tools.eventscollector.io.timing.TimingResponse;
import net.alemas.oss.tools.eventscollector.repositories.TimerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;


/**
 * API controller to collect timing events;
 *
 * Created by MASCHERPA on 26/05/2021.
 */
@RestController
@RequestMapping( "${server.base-path}/events/timings" )
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
    @Autowired
    private TimerRepository     repository;

    /**
     * server configuration properties;
     */
    @Autowired
    private ServerConfiguration properties;


    /* --- constructors --- */
    /* none */

    /* --- end-points --- */
    @ApiOperation
            (
                    value = "To store a timer event.",
                    code = 204
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse( code = 204, message = "Event stored" ),
                                    @ApiResponse( code = 404, message = "If client posted an invalid event." )
                            }
            )
    @RequestMapping
            (
                    value		= "/",
                    method		= RequestMethod.POST,
                    consumes	= MediaType.APPLICATION_FORM_URLENCODED_VALUE
            )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    private void addCounter( TimingEvent event )
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
                    value       = "/",
                    method      = RequestMethod.GET,
                    produces    = MediaType.APPLICATION_JSON_VALUE
            )
    @ResponseStatus( HttpStatus.OK )
    private Flux< TimingResponse > getEventsGroupedById()
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "returning list of events as json objects array." );
        }
        return
                this.repository
                        .groupById()
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
                    value       = "/spreadsheet-ml",
                    method      = RequestMethod.GET,
                    produces    = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
    private ResponseEntity< Mono< Resource > > getEventsGroupedByIdAsSpreadsheet()
            throws
                IOException
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "returning list of timers in spreadsheet file" );
        }

        String name =
                this.properties
                        .getFileNameSpreadsheet()
                ;

        return
                ResponseEntity
                        .ok()
                        .header( HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" )
                        .header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name + ".timers.xlsx" )
                        .cacheControl( CacheControl.noCache() )
                        .body
                                (
                                        Mono.fromCallable
                                                (
                                                        () ->
                                                                SpreadsheetTimers
                                                                        .getInstance()
                                                                        .export
                                                                                (
                                                                                        this.repository.groupById()
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
