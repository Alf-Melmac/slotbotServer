package de.webalf.slotbot.model.paging;

import lombok.Getter;
import lombok.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * @author Alf
 * @since 22.12.2020
 */
@Getter
@Value
public class Paging {
	int page;
	int size;
	String filter;
	Sort sort;

	public Pageable getPageable() {
		return PageRequest.of(page, size, sort.getSort());
	}
}
