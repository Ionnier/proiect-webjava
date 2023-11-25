package org.dbahrim.forum.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;



@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {
    public static final String BASE_PATH = "data_api";

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.disableDefaultExposure();
        config.setExposeRepositoryMethodsByDefault(true);
        config.setBasePath(BASE_PATH);
    }
}
