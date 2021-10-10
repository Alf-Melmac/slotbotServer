package de.webalf.slotbot.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
import java.util.Map;

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
	private String prefix = "!";

	private Map<Long, Long> archive;

	/**
	 * Returns the configured archive channel for the given guild
	 *
	 * @param guildId in which the event will be archived
	 * @return channel id of the archive channel
	 */
	public Long getArchive(long guildId) {
		return archive.get(guildId);
	}
}
