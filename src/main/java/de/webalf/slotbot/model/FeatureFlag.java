package de.webalf.slotbot.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 11.07.2024
 */
@Entity
@Table(name = "feature_flag", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class FeatureFlag extends AbstractSuperIdEntity {
	@Column(name = "feature_flag_feature")
	private String feature;

	@ManyToOne
	@JoinColumn(name = "feature_flag_user", foreignKey = @ForeignKey(name = "feature_flag_user_fk"))
	private User user;

	@ManyToOne
	@JoinColumn(name = "feature_flag_guild", foreignKey = @ForeignKey(name = "feature_flag_guild_fk"))
	private Guild guild;
}
