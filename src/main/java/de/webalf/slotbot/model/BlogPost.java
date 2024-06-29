package de.webalf.slotbot.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
	@Column(name = "blog_post_content", columnDefinition = "text", nullable = false)
	@NonNull
	private String content;

	@Column(name = "blog_post_pinned", nullable = false)
	@Builder.Default
	private boolean pinned = false;

	@ManyToOne
	@JoinColumn(name = "guild_id")
	@NonNull
	private Guild guild;
}
