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
	/**
	 * The token of the discord bot. Found in the <a href="https://discord.com/developers/applications">Discord Developer Portal</a>
	 */
	@NotBlank
	private String token;

	/**
	 * The public key of the discord application. Found in the <a href="https://discord.com/developers/applications">Discord Developer Portal</a>
	 */
	@NotBlank
	private String publicKey;
}
