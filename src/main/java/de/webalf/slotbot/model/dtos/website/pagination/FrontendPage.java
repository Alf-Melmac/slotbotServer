package de.webalf.slotbot.model.dtos.website.pagination;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.domain.Page;

/**
 * Page metadata used by the frontend. Replacement for {@link org.springframework.hateoas.PagedModel.PageMetadata}
 *
 * @author Alf
 * @since 28.02.2023
 */
@Value
@Builder(access = AccessLevel.PRIVATE)
public class FrontendPage {
	/**
	 * Size of the payload
	 */
	int size;

	/**
	 * Total count of elements
	 */
	long totalElements;

	/**
	 * Count of available pages
	 */
	int totalPages;

	/**
	 * Current page number
	 */
	int number;

	/**
	 * Creates a {@link FrontendPage} object by the given page
	 */
	static FrontendPage of(@NonNull Page<?> page) {
		return FrontendPage.builder()
				.size(page.getSize())
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.number(page.getNumber())
				.build();
	}
}
