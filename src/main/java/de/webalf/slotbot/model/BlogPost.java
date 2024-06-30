package de.webalf.slotbot.model;

import de.webalf.slotbot.converter.persistence.LocalDateTimePersistenceConverter;
import de.webalf.slotbot.util.DateUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author Alf
 * @since 30.06.2024
 */
@Entity
@Table(name = "blog_post", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class BlogPost extends AbstractSuperIdEntity {
	@Column(name = "blog_post_timestamp", nullable = false)
	@NotNull
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Builder.Default
	private LocalDateTime timestamp = DateUtils.now();

	@Column(name = "blog_post_content", columnDefinition = "text", nullable = false)
	@NotNull
	private String content;

	@Column(name = "blog_post_pinned", nullable = false)
	@Builder.Default
	private boolean pinned = false;

	@ManyToOne
	@JoinColumn(name = "guild_id")
	@NotNull
	private Guild guild;
}
