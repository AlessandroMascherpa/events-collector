package net.alemas.oss.tools.eventscollectorclient.configuration;





import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Configuration properties read from application.yml.
 *
 * Created by MASCHERPA on 16/03/2021.
 */
public class ClientProperties
{
    /* --- logging --- */
    private static final Log log = LogFactory.getLog( ClientProperties.class );

    /* --- properties --- */
    private String  basePath;

    /* --- constructors --- */
    public ClientProperties()
    {
        final String    propertiesFile  = "/client.properties";
        final String    propertiesField = "server.base-path";

        try
                (
                        InputStream input =
                                this
                                        .getClass()
                                        .getResourceAsStream( propertiesFile )
                )
        {
            if ( input != null )
            {
                Properties properties = new Properties();

                properties.load( input );

                this.basePath = properties.getProperty( propertiesField );
            }
            else
            {
                log.error( "Properties file '" + propertiesFile + "' not found." );
            }
        }
        catch ( IOException e )
        {
            log.error( "Failed to read from properties file '" + propertiesFile + "'.", e );
        }
        if ( this.basePath == null )
        {
            this.basePath = "";
            log.error( "Properties file '" + propertiesFile + "' not found or entry '" + propertiesField + "' not defined. Set base path as empty string." );
        }
    }

    /* --- getters --- */
    public String getBasePath()
    {
        return this.basePath;
    }

}
