package de.webalf.slotbot.configuration.springdoc;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.NonNull;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Springdoc configuration. For resulting {@link OpenAPI} configuration see {@link OpenApiConfig}.
 *
 * @author Alf
 * @since 11.06.2023
 */
@Configuration
public class SpringDocConfig {
	@Bean
	SpringDocConfigProperties springDocConfigProperties(@NonNull SpringDocConfigProperties config) {
		config.setPackagesToScan(List.of("de.webalf.slotbot.controller.api"));
		return config;
	}
}
