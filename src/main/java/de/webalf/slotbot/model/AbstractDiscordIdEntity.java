package de.webalf.slotbot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Identical to {@link AbstractSuperIdEntity}, but does not generate ids and expects Discord snowflakes
 *
 * @author Alf
 * @since 04.01.2022
 */
@MappedSuperclass
@Getter
@RequiredArgsConstructor
@SuperBuilder
public abstract class AbstractDiscordIdEntity {
	@Id
	@Column(name = "id", nullable = false, unique = true, updatable = false)
	protected long id;
}
