package de.webalf.slotbot.model;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * @author Alf
 * @since 22.06.2020
 */
@MappedSuperclass
@Getter
public abstract class AbstractIdEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, unique = true, updatable = false)
	protected long id;
}
