package de.webalf.slotbot.model.dtos.website.pagination;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * This model provides the expected paginated format for the frontend and is a replacement for {@link org.springframework.hateoas.PagedModel}
 *
 * @param <T> type of the content, usually DTOs
 * @author Alf
 * @since 28.02.2023
 */
@Builder(access = AccessLevel.PRIVATE)
@Value
public class FrontendPageable<T> {
	/**
	 * Info about this page
	 */
	FrontendPage page;
	/**
	 * Actual page content
	 */
	List<T> content;

	/**
	 * Creates a {@link FrontendPageable} object by the given page
	 */
	public static <T> FrontendPageable<T> of(@NonNull Page<T> page) {
		final List<T> content = page.getContent();
		return FrontendPageable.<T>builder()
				.content(content)
				.page(FrontendPage.of(page))
				.build();
	}
}
