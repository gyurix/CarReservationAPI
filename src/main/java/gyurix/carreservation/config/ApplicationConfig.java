package gyurix.carreservation.config;

import gyurix.carreservation.db.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class that sets up application-wide configurations.
 */
@Configuration
@EntityScan(basePackages = {
        "gyurix.carreservation.db.entities"
})
@EnableJpaRepositories(basePackages = {
        "gyurix.carreservation.db.repositories"
})
@RequiredArgsConstructor
public class ApplicationConfig implements WebMvcConfigurer {
    /**
     * Configures the Cross-Origin Resource Sharing (CORS) for the application.
     *
     * @return The configured WebMvcConfigurer instance.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }

    /**
     * Creates an instance of the EntityMapper using MapStruct.
     *
     * @return The created EntityMapper instance.
     */
    @Bean
    public EntityMapper entityMapper() {
        return Mappers.getMapper(EntityMapper.class);
    }
}
