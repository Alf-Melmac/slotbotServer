package de.webalf.slotbot.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author Alf
 * @since 09.01.2022
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
	@Value("${server.cors.allowed-origins}")
	private String[] allowedOrigins;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedMethods(GET.name(), POST.name(), PUT.name(), DELETE.name())
				.allowedOrigins(allowedOrigins)
				.allowCredentials(true);
	}
}
