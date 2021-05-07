package net.alemas.oss.tools.eventscollector.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * Configuration class for Swagger 2.
 *
 * Created by MASCHERPA on 05/03/2021.
 */
@Configuration
@EnableSwagger2
public class SwaggerUiConfiguration
{
    /* --- properties --- */
    @SuppressWarnings( "SpringJavaAutowiringInspection" )
    @Autowired
    private ServerConfiguration properties;

    /* --- swagger methods --- */
    @Bean
    public Docket api()
    {
        return
                new Docket( DocumentationType.SWAGGER_2 )
                        .apiInfo
                                (
                                        new ApiInfoBuilder()
                                                .title( this.properties.getTitle() )
                                                .version( this.properties.getVersion() )
                                                .description( this.properties.getDescription() )
                                                .build()
                                )
//---                        .host( this.properties.getHostName() )
                        .useDefaultResponseMessages( false )
                        .select()
                        .apis( RequestHandlerSelectors.any() )
                        .paths( PathSelectors.any() )
                        .build()
                ;
    }

}
