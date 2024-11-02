package de.webalf.slotbot.model;

import de.webalf.slotbot.converter.persistence.LocalDateTimePersistenceConverter;
import de.webalf.slotbot.util.DateUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static de.webalf.slotbot.util.ConstraintConstants.REASON;
import static de.webalf.slotbot.util.ConstraintConstants.REASON_DB;

/**
 * @author Alf
 * @since 13.10.24
 */
@Entity
@Table(name = "ban", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}),
		@UniqueConstraint(columnNames = {"ban_user", "ban_guild"}, name = "user_guild_unique")})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Ban extends AbstractSuperIdEntity {
	@ManyToOne(targetEntity = User.class, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "ban_user", nullable = false, foreignKey = @ForeignKey(name = "ban_user_fk"))
	@NotNull
	private User user;

	@ManyToOne(targetEntity = Guild.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "ban_guild", foreignKey = @ForeignKey(name = "ban_guild_fk"))
	private Guild guild;

	@Column(name = "ban_reason", length = REASON_DB)
	@Size(max = REASON)
	private String reason;

	@Column(name = "ban_timestamp", nullable = false)
	@NotNull
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Builder.Default
	private LocalDateTime timestamp = DateUtils.now();
}
