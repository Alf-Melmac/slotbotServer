package de.webalf.slotbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 08.04.2021
 */
@MappedSuperclass
@Getter
@RequiredArgsConstructor
@SuperBuilder
public abstract class AbstractSuperIdEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, unique = true, updatable = false)
	protected long id;
}
