package net.alemas.oss.tools.eventscollector.controllers;


import net.alemas.oss.tools.eventscollector.io.LogInOut;
import net.alemas.oss.tools.eventscollector.io.LogInOutEvent;
import net.alemas.oss.tools.eventscollector.io.LogInOutResponse;
import net.alemas.oss.tools.eventscollector.repositories.LoginRepository;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


/**
 * API controller devoted to log in and out events;
 *
 * Created by MASCHERPA on 04/02/2021.
 */
@RestController
@RequestMapping( "${server.base-path}/events/logs-in-out" )
@Api
        (
                description = "API controller to collect log in/out events and to provide statistics about events."
        )
public class LoginController
{
    /* --- logging --- */
    final private static Logger log = LoggerFactory.getLogger( LoginController.class );

    /* --- properties --- */
    /*
     * add here any data repository that supports non-blocking reactive streams;
     */
    @Autowired
    private LoginRepository repository;

    /* --- constructors --- */
    /* none */

    /* --- end-points --- */
    @ApiOperation
            (
                    value = "Get one single event from the events collection.",
                    code = 200
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse( code = 200, message = "Event found and got." ),
                                    @ApiResponse( code = 404, message = "The requested event was not found." )
                            }
            )
    @RequestMapping
            (
                    value       = "/{row}",
                    method      = RequestMethod.GET,
                    produces    = MediaType.APPLICATION_JSON_VALUE
            )
    private Mono< LogInOutEvent > getLogInOutByRowNumber
            (
                    @ApiParam
                            (
                                    value           = "The event ordinal number.",
                                    required        = true,
                                    allowableValues = "range[0, infinity]",
                                    allowEmptyValue = false
                            )
                    @PathVariable int row
            )
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "getting row - row number: {} - begin", row );
        }
        Mono< LogInOutEvent > tuple;
        try
        {
            tuple = this.repository.getByRowNumber( row );
        }
        catch ( Exception e )
        {
            if ( log.isWarnEnabled() )
            {
                log.warn( "getting row - failure - row number: " + row, e );
            }
            throw
                    new ResponseStatusException
                            (
                                    HttpStatus.NOT_FOUND,
                                    e.getMessage(),
                                    e
                            )
                    ;
        }
        if ( log.isInfoEnabled() )
        {
            log.info( "getting row - end" );
        }
        return
                tuple;
    }


    @ApiOperation
            (
                    value = "List stored log in/out events. Events can be filtered out with query parameters.",
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
    private Flux< LogInOutResponse > getAllLogsInOut
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
                                    value           = "To list events after the given date/time; the date/time pattern is: '" + LogInOut.DATE_TIME_PATTERN + "'.",
                                    format          = LogInOut.DATE_TIME_PATTERN,
                                    required        = false,
                                    allowEmptyValue = false
                            )
                    @RequestParam( required = false )
                    @DateTimeFormat( pattern = LogInOut.DATE_TIME_PATTERN )
                    LocalDateTime after,

                    @ApiParam
                            (
                                    value           = "To list events before the given date/time; the date/time pattern is: '" + LogInOut.DATE_TIME_PATTERN + "'.",
                                    format          = LogInOut.DATE_TIME_PATTERN,
                                    required        = false,
                                    allowEmptyValue = false
                            )
                    @RequestParam( required = false )
                    @DateTimeFormat( pattern = LogInOut.DATE_TIME_PATTERN )
                    LocalDateTime before
            )
    {
        if ( log.isInfoEnabled() )
        {
            log.info
                    (
                            "returning list of events - filter by: application: '{}', after: {}, before: {}",
                            application,
                            LogInOut.convertDate( after ),
                            LogInOut.convertDate( before )
                    );
        }
        return
                this.repository.list
                        (
                                application,
                                after,
                                before
                        );
    }
/*
    @RequestMapping
            (
                    value       = "/spreadsheet-ml",
                    method      = RequestMethod.GET,
                    produces    = { MediaType.APPLICATION_OCTET_STREAM_VALUE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" }
            )
    private Flux< LogInOutResponse > getSpreadsheetLogsInOut()
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "returning list of events as spreadsheet" );
        }
        return
                ++++
                this.repository.list();
    }
*/
    @ApiOperation
            (
                    value = "To store a log in/out event.",
                    code = 204
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse( code = 204, message = "Event stored" ),
                                    @ApiResponse( code = 404, message = "If client posted an invalid log in/out event." )
                            }
            )
    @RequestMapping
            (
                    value		= "/",
                    method		= RequestMethod.POST,
                    consumes	= MediaType.APPLICATION_FORM_URLENCODED_VALUE
            )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    private void addLogin( LogInOutEvent event )
    {
        if ( log.isInfoEnabled() )
        {
            log.info( "logging event - event: '{}' - begin", event.toString() );
        }
        try
        {
            this.repository.add( event );
        }
        catch ( Exception e )
        {
            if ( log.isWarnEnabled() )
            {
                log.warn( "logging event - failure", e );
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
            log.info( "logging event - end" );
        }
    }

}
