package de.webalf.slotbot.service.web;

import lombok.experimental.UtilityClass;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author Alf
 * @since 24.09.2024
 */
@UtilityClass
public class TenantContext {
	public static String getTenant() {
		final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			return null;
		}
		return (String) requestAttributes.getAttribute("tenant", RequestAttributes.SCOPE_REQUEST);
	}
}
