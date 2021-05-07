package net.alemas.oss.tools.eventscollector.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;



/**
 * Server configuration properties read from application.yml.
 *
 * Created by MASCHERPA on 16/03/2021.
 */
@Configuration
public class ServerConfiguration
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

    @Value( "${server.base-path}" )
    private String  basePath;

    private String  fileNameSpreadsheet;

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

    public String getBasePath()
    {
        return this.basePath;
    }

    public String getFileNameSpreadsheet()
    {
        if ( this.fileNameSpreadsheet == null )
        {
            // remove heading slash from path;
            this.fileNameSpreadsheet = this.basePath.substring( 1 );
        }
        return
                this.fileNameSpreadsheet;
    }
}
