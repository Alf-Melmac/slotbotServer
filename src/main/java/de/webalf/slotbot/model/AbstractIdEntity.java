package de.webalf.slotbot.model;

import lombok.Getter;

import javax.persistence.*;

/**
 * @author Alf
 * @since 22.06.2020
 */
@MappedSuperclass
@Getter
public abstract class AbstractIdEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	protected long id;
}
