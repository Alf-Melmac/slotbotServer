package de.webalf.slotbot.feature.ip_filter;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Disable Private Relay IP range checking in dev profile.
 *
 * @author Alf
 * @since 18.10.2025
 */
@Service
@Profile("dev")
class PrivateRelayRangeDevService implements PrivateRelayRangeService {
	public boolean isFromPrivateRelay(String ip) {
		return false;
	}
}
