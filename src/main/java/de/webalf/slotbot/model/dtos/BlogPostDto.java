package de.webalf.slotbot.model.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 30.06.2024
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BlogPostDto extends AbstractIdEntityDto {
	private String content;
	private boolean pinned;
}
