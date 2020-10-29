package de.webalf.slotbot.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

/**
 * @author Alf
 * @since 29.10.2020
 */
@Configuration
@ConfigurationProperties("slotbot.discord")
@Getter
@Setter
public class DiscordProperties {
	@NotBlank
	private String token;

	@NotBlank
	private String guild;
}
