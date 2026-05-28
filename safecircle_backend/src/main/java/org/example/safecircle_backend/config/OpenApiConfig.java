package org.example.safecircle_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI safecircleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SafeCircle API")
                        .description("Anonymous SRH support platform for youth in Rwanda")
                        .version("1.0.0"));
    }
}
