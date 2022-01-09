package de.webalf.slotbot.configuration;

import de.webalf.slotbot.service.ControllerAdviser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Alf
 * @since 09.01.2022
 */
@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WebMvcConfig implements WebMvcConfigurer {
	private final ControllerAdviser controllerAdviser;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(controllerAdviser);
	}
}
