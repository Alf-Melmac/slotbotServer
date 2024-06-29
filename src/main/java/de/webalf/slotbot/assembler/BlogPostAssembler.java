package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.BlogPost;
import de.webalf.slotbot.model.dtos.BlogPostDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 30.06.2024
 */
@UtilityClass
public final class BlogPostAssembler {
	private static BlogPostDto toDto(@NonNull BlogPost blogPost) {
		return BlogPostDto.builder()
				.id(blogPost.getId())
				.content(blogPost.getContent())
				.pinned(blogPost.isPinned())
				.build();
	}

	public static List<BlogPostDto> toDtoList(Iterable<? extends BlogPost> blogPosts) {
		return StreamSupport.stream(blogPosts.spliterator(), false)
				.map(BlogPostAssembler::toDto)
				.toList();
	}
}
