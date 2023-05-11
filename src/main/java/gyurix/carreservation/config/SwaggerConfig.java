package gyurix.carreservation.config;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.TreeMap;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

/**
 * Configuration class that sets up Swagger documentation for the API.
 */
@Configuration
public class SwaggerConfig {
    /**
     * Configures the Swagger UI page information.
     *
     * @return The OpenApiCustomiser instance with the configured Swagger UI page information.
     */
    @Bean
    public OpenApiCustomiser configureSwaggerPage() {
        return openApi -> openApi.info(openApi.getInfo().title("CarReservationAPI"))
                .getComponents().setSchemas(new TreeMap<>(openApi.getComponents().getSchemas()));
    }

    /**
     * Configures a redirect from the root endpoint ("/") to the Swagger UI page.
     *
     * @return The RouterFunction instance for redirecting to the Swagger UI page.
     */
    @Bean
    RouterFunction<ServerResponse> redirectRootEndpoint() {
        return route(GET("/"), req ->
                ServerResponse.permanentRedirect(URI.create("swagger-ui.html")).build());
    }

    /**
     * Configures a redirect from the "/swagger" endpoint to the Swagger UI page.
     *
     * @return The RouterFunction instance for redirecting to the Swagger UI page.
     */
    @Bean
    RouterFunction<ServerResponse> redirectSwaggerEndpoint() {
        return route(GET("/swagger**"), req ->
                ServerResponse.permanentRedirect(URI.create("swagger-ui.html")).build());
    }
}
