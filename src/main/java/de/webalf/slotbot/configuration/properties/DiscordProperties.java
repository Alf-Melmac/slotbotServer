package de.webalf.slotbot.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

	@NotBlank
	private String guild;

	@NotBlank
	private String prefix = "!";

	@NotNull
	private long id;

	private Long archive;

	private Long modLog;
}
