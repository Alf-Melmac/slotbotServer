package de.webalf.slotbot.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Alf
 * @since 29.10.2020
 */
@ConfigurationProperties("discord")
@Getter
@Setter
public class DiscordProperties {
	@NotBlank
	private String token;
}
