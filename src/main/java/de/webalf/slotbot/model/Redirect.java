package de.webalf.slotbot.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

/**
 * @author Alf
 * @since 03.12.2020
 */
@Entity
@Table(name = "redirect", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}), @UniqueConstraint(columnNames = {"endpoint"})})
@Immutable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Redirect extends AbstractIdEntity {
	@SuppressWarnings("unused") //Used in RedirectRepository
	@Column(name = "endpoint")
	private String endpoint;

	@Column(name = "link")
	@NotBlank
	@Getter
	private String link;

	@Builder
	private Redirect(@NotBlank String link) {
		this.link = link;
	}
}
