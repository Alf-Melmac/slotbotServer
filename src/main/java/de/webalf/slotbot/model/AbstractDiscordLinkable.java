package de.webalf.slotbot.model;

import de.webalf.slotbot.util.LongUtils;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * In addition to an {@link AbstractSuperIdEntity id}, entities extending this class can be linked to a Discord entity via a nullable Discord snowflake
 *
 * @author Alf
 * @since 19.07.26
 */
@MappedSuperclass
@Getter
@RequiredArgsConstructor
@SuperBuilder
public abstract class AbstractDiscordLinkable extends AbstractSuperIdEntity {
	/**
	 * Optional linked Discord snowflake
	 */
	@Column(name = "discord_id")
	private Long discordId;

	/**
	 * Discord-linkable entities can have the Discord snowflake as their id. {@link #getDiscordIdString() Snowflakes
	 * are too large for JavaScript numbers}, so this method returns the id as string.
	 *
	 * @return id as string
	 */
	public String getIdString() {
		return Long.toString(id);
	}

	/**
	 * Discord snowflakes exceed the <a href="https://stackoverflow.com/questions/1379934/large-numbers-erroneously-rounded-in-javascript">
	 * maximum size of a JavaScript number</a>. Therefore, this method returns the id as string.
	 *
	 * @return discord id as string
	 */
	public String getDiscordIdString() {
		return LongUtils.toString(discordId);
	}
}
