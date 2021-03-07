package de.webalf.slotbot.util.permissions;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

import static de.webalf.slotbot.constant.AuthorizationCheckValues.*;
import static de.webalf.slotbot.model.authentication.ApiTokenType.TypeRoleNames.*;

/**
 * @author Alf
 * @since 06.03.2021
 */
@UtilityClass
public final class ApiPermissionHelper {
	public static final String HAS_READ_PUBLIC_PERMISSION = HAS_ANY_ROLE + READ_PUBLIC + HAS_ANY_ROLE_NEXT + READ + HAS_ANY_ROLE_NEXT + WRITE + HAS_ROLE_CLOSE;
	public static final String HAS_READ_PERMISSION = HAS_ANY_ROLE + READ + HAS_ANY_ROLE_NEXT + WRITE + HAS_ROLE_CLOSE;
	public static final String HAS_WRITE_PERMISSION = HAS_ROLE + WRITE + HAS_ROLE_CLOSE;

	private static final Set<String> READ_ROLES = Set.of(READ, WRITE);

	public static boolean hasReadPermission() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.anyMatch(grantedAuthority -> READ_ROLES.contains(grantedAuthority.getAuthority()));
	}
}
