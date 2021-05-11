package net.alemas.oss.tools.eventscollector.controllers;


import net.alemas.oss.tools.eventscollector.configuration.ServerConfiguration;
import net.alemas.oss.tools.eventscollector.io.CounterEvent;
import net.alemas.oss.tools.eventscollector.repositories.CounterRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


/**
 * API controller to collect single events;
 *
 * Created by MASCHERPA on 06/05/2021.
 */
@RestController
@RequestMapping( "${server.base-path}/events/counter" )
@Api
        (
                description = "API controller to collect single events and to provide statistics about them."
        )
public class CounterController
{
    /* --- logging --- */
    final private static Logger log = LoggerFactory.getLogger( CounterController.class );

    /* --- properties --- */
    /*
     * add here any data repository that supports non-blocking reactive streams;
     */
    @Autowired
    private CounterRepository       repository;

    /**
     * server configuration properties;
     */
    @Autowired
    private ServerConfiguration     properties;


    /* --- constructors --- */
    /* none */

    /* --- end-points --- */
    @ApiOperation
            (
                    value = "To store a single event.",
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
    private void addCounter( CounterEvent event )
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
