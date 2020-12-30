package de.webalf.slotbot.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

/**
 * @author Alf
 * @since 29.12.2020
 */
@Configuration
@ConfigurationProperties("slotbot.api")
@Getter
@Setter
public class SlotbotApiProperties {
	@NotBlank
	private final String name = "slotbot-authorization";

	@NotBlank
	private String url;

	@NotBlank
	private String token;
}
