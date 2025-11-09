package de.webalf.slotbot.feature.ip_filter;

/**
 * Loads and stores Apple iCloud Private Relay IP ranges and provides membership checks.
 *
 * @author Alf
 * @see <a href="https://developer.apple.com/icloud/prepare-your-network-for-icloud-private-relay">Prepare your web server for iCloud Private Relay</a>
 * @since 06.10.2025
 */
interface PrivateRelayRangeService {
	/**
	 * Checks if the given ip is part of the Private Relay ranges.
	 *
	 * @param ip to check
	 * @return true if ip is part of the Private Relay ranges, false otherwise
	 */
	boolean isFromPrivateRelay(String ip);
}
