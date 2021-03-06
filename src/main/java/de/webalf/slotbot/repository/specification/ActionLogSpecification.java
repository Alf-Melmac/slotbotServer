package de.webalf.slotbot.repository.specification;

import de.webalf.slotbot.model.ActionLog;
import de.webalf.slotbot.model.ActionLog_;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.User_;
import de.webalf.slotbot.util.CollectionUtils;
import de.webalf.slotbot.util.StringUtils;
import de.webalf.slotbot.util.WildcardSpecificationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link de.webalf.slotbot.model.ActionLog} specification
 *
 * @author Alf
 * @since 22.12.2020
 */
@RequiredArgsConstructor
public class ActionLogSpecification implements Specification<ActionLog> {
	private final String filter;

	@Override
	public Predicate toPredicate(Root<ActionLog> actionLog, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> filterPredicates = new ArrayList<>();

		if (StringUtils.isNotEmpty(filter)) {
			String wildcardTerm = WildcardSpecificationUtils.buildLowerCaseWildcardParam(filter);

			Join<ActionLog, User> logUser = actionLog.join(ActionLog_.user);
			filterPredicates.add(cb.like(cb.lower(logUser.get(User_.id).as(String.class)), wildcardTerm));
			filterPredicates.add(cb.like(cb.lower(actionLog.get(ActionLog_.action).as(String.class)), wildcardTerm));
		}

		return CollectionUtils.isNotEmpty(filterPredicates) ? cb.or(filterPredicates.toArray(new Predicate[0])) : null;
	}
}
