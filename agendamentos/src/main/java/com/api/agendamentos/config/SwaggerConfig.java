package com.api.agendamentos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customAPI() {
        return new OpenAPI()
                .info(new Info().title("Agendamentos API")
                        .description("API para gerenciamento dos agendamentos de uma farmácia.")
                        .version("v0.0.1-SNAPSHOT")
                        .license(new License().name("Apache 2.0").url("http://apache.org/licenses/LICENSE-2.0.html")));
    }
}
