package net.alemas.oss.tools.eventscollector.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Configuration properties read from application.yml.
 *
 * Created by MASCHERPA on 16/03/2021.
 */
@Component
public class Properties
{
    /* --- properties --- */
    @Value( "${server.base-path}" )
    private String  basePath;

    /* --- getters --- */
    public String getBasePath()
    {
        return this.basePath;
    }

}
