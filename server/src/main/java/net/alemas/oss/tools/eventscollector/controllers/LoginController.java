package net.alemas.oss.tools.eventscollector.controllers;


import io.swagger.annotations.*;
import net.alemas.oss.tools.eventscollector.configuration.EndpointsPaths;
import net.alemas.oss.tools.eventscollector.configuration.ServerConfiguration;
import net.alemas.oss.tools.eventscollector.exporters.SpreadsheetLogins;
import net.alemas.oss.tools.eventscollector.io.Base;
import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutEvent;
import net.alemas.oss.tools.eventscollector.io.loginout.LogInOutResponse;
import net.alemas.oss.tools.eventscollector.repositories.LoginRepository;
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
 * API controller devoted to log in and out events;
 *
 * Created by MASCHERPA on 04/02/2021.
 */
@RestController
@RequestMapping( "${server.base-path}" + EndpointsPaths.REQUEST_PATH_LOGIN_BASE )
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
    private final LoginRepository     repository;

    /**
     * server configuration properties;
     */
    private final ServerConfiguration properties;

    /**
     * spread sheet exporter;
     */
    private final SpreadsheetLogins   exporter;


    /* --- constructors --- */
    @Autowired
    public LoginController
        (
                LoginRepository     repository,
                ServerConfiguration properties,
                SpreadsheetLogins   exporter
        )
    {
        this.repository = repository;
        this.properties = properties;
        this.exporter   = exporter;
    }

    /* --- end-points --- */
    @ApiOperation
            (
                    value = "List stored log in/out events as JSON objects array. Events can be filtered out with query parameters.",
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
                    value       = EndpointsPaths.REQUEST_PATH_LOGIN_EVENT,
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
            log.info
                    (
                            "returning list of events as json objects array - filter by: application: '{}', after: {}, before: {}",
                            Base.safeLogString( application ),
                            Base.convertDate( after ).replaceAll( "[\r\n]", "" ),
                            Base.convertDate( before ).replaceAll( "[\r\n]", "" )
                    );
        }
        return
                this.repository.groupById
                        (
                                application,
                                after,
                                before
                        );
    }


    @ApiOperation
            (
                    value = "List stored log in/out events as spreadsheet file. Events can be filtered out with query parameters.",
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
                    value       = EndpointsPaths.REQUEST_PATH_LOGIN_EVENT_XLSX,
                    method      = RequestMethod.GET
            )
    private ResponseEntity< Mono< Resource > >  getSpreadsheetLogsInOut
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
            log.info
                    (
                            "returning list of events as spreadsheet - filter by: application: '{}', after: {}, before: {}",
                            Base.safeLogString( application ),
                            Base.convertDate( after ).replaceAll( "[\r\n]", "" ),
                            Base.convertDate( before ).replaceAll( "[\r\n]", "" )
                    );
        }

        String name =
                this.properties
                        .getFileNameSpreadsheet()
                ;

        ContentDisposition attachment =
                ContentDisposition
                        .attachment()
                        .filename( name + '.' + EndpointsPaths.EXPORTED_FILE_NAME_LOGIN )
                        .build()
                ;
        return
                ResponseEntity
                        .ok()
                        .header( HttpHeaders.CONTENT_TYPE, EndpointsPaths.APPLICATION_SPREADSHEET )
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


    @ApiOperation
            (
                    value = "To store a log in/out event.",
                    code = 201
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse( code = 201, message = "Event stored" ),
                                    @ApiResponse( code = 404, message = "If client posted an invalid log in/out event." )
                            }
            )
    @RequestMapping
            (
                    value		= EndpointsPaths.REQUEST_PATH_LOGIN_EVENT,
                    method		= RequestMethod.POST,
                    consumes	= MediaType.APPLICATION_JSON_VALUE
            )
    @ResponseStatus( HttpStatus.CREATED )
    private void addLogin
            (
                    @RequestBody LogInOutEvent event
            )
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
