package de.webalf.slotbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Identical to {@link AbstractSuperIdEntity}, but does not generate ids and expects Discord snowflakes
 *
 * @author Alf
 * @since 04.01.2022
 * @deprecated Migrate to {@link AbstractDiscordLinkable}
 */
@MappedSuperclass
@Getter
@RequiredArgsConstructor
@SuperBuilder
@Deprecated
public abstract class AbstractDiscordIdEntity implements HasId {
	@Id
	@Column(name = "id", nullable = false, unique = true, updatable = false)
	protected long id;

	/**
	 * Discord snowflakes exceed the <a href="https://stackoverflow.com/questions/1379934/large-numbers-erroneously-rounded-in-javascript">
	 * maximum size of a JavaScript number</a>. Therefore, this method returns the id as string.
	 *
	 * @return id as string
	 */
	public String getIdString() {
		return Long.toString(id);
	}
}
