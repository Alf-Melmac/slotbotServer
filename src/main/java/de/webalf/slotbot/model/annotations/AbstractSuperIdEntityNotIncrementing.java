package de.webalf.slotbot.model.annotations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Alf
 * @since 08.04.2021
 */
@MappedSuperclass
@Getter
@RequiredArgsConstructor
@SuperBuilder
public abstract class AbstractSuperIdEntityNotIncrementing {
	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, unique = true, updatable = false)
	protected long id;
}
