package net.alemas.oss.tools.eventscollector.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



/**
 * Server configuration properties read from application.yml.
 *
 * Created by MASCHERPA on 16/03/2021.
 */
@Component
public class ServerProperties
{
    /* --- properties --- */
    @Value( "${server.title}" )
    private String  title;

    @Value( "${server.description}" )
    private String  description;

    @Value( "${server.version}" )
    private String  version;

    @Value( "${server.the-host-name}" )
    private String  hostName;

    /* --- getters --- */
    public String getTitle()
    {
        return this.title;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getVersion()
    {
        return this.version;
    }

    public String getHostName()
    {
        return this.hostName;
    }

}
