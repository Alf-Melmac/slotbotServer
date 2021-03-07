package de.webalf.slotbot.model.authentication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * @author Alf
 * @since 06.03.2021
 */
@Entity
@Table(name = "api_token", uniqueConstraints = {@UniqueConstraint(columnNames = {"token"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiToken {
	@Id
	@Column(name = "token")
	@NotBlank
	private String token;

	@Column(name = "api_token_type")
	@Enumerated(EnumType.STRING)
	@NotBlank
	private ApiTokenType type;

	@Column(name = "api_token_comment")
	private String comment;
}
