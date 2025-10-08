package de.webalf.slotbot.feature.ip_filter;

import de.webalf.slotbot.util.StringUtils;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.ipv4.IPv4AddressTrie;
import inet.ipaddr.ipv6.IPv6AddressTrie;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

/**
 * Loads and stores Apple iCloud Private Relay IP ranges and provides membership checks.
 *
 * @author Alf
 * @see <a href="https://developer.apple.com/icloud/prepare-your-network-for-icloud-private-relay">Prepare your web server for iCloud Private Relay</a>
 * @since 06.10.2025
 */
@Service
@Slf4j
public class PrivateRelayRangeService {
	private static final String SOURCE_URL = "egress-ip-ranges.csv";
	private static final RestClient REST_CLIENT = RestClient.create("https://mask-api.icloud.com");

	// Check if there is already a more space-saving way of storing these: https://github.com/seancfoley/IPAddress/issues/135
	private IPv4AddressTrie iPv4AddressTrie = null;
	private IPv6AddressTrie iPv6AddressTrie = null;

	/**
	 * Refreshes the Private Relay IP ranges and stores them in tries for fast lookup.
	 */
	@Scheduled(fixedDelay = 6, timeUnit = TimeUnit.HOURS)
	public void refreshRanges() {
		final StopWatch watch = new StopWatch();
		watch.start();
		final String csv = REST_CLIENT
				.get()
				.uri(SOURCE_URL)
				.retrieve()
				.onStatus(HttpStatusCode::isError, (request, response) ->
						log.error("Failed to download Private Relay IP ranges: {} {}", response.getStatusCode(), new String(response.getBody().readAllBytes())))
				.body(String.class);
		final Pair<IPv4AddressTrie, IPv6AddressTrie> ipRanges = parseRanges(csv);
		watch.stop();
		if (ipRanges.getLeft() == null || ipRanges.getRight() == null) {
			log.warn("CSV parsed but produced 0 ranges in {} ms. Keeping previous", watch.getTotalTimeMillis());
			return;
		}
		this.iPv4AddressTrie = ipRanges.getLeft();
		this.iPv6AddressTrie = ipRanges.getRight();
		log.info("Loaded {} ipv4 and {} ipv6 Private Relay IP ranges in {} ms", iPv4AddressTrie.size(), iPv6AddressTrie.size(), watch.getTotalTimeMillis());
	}

	/**
	 * Parses the (large) csv string into two tries for ipv4 and ipv6 addresses.
	 * <p>
	 * The csv is expected to have the cidr ranges in the first column separated by a comma.
	 *
	 * @param csv containing ip ranges
	 * @return pair of ipv4 and ipv6 tries or null pair if input is empty or parsing failed
	 */
	private static Pair<IPv4AddressTrie, IPv6AddressTrie> parseRanges(String csv) {
		if (StringUtils.isEmpty(csv)) {
			return ImmutablePair.nullPair();
		}

		final IPv4AddressTrie iPv4Addresses = new IPv4AddressTrie();
		final IPv6AddressTrie iPv6Addresses = new IPv6AddressTrie();
		try (final BufferedReader reader = new BufferedReader(new StringReader(csv))) {
			reader.lines()
					.map(line -> line.substring(0, line.indexOf(',')))
					.forEach(ip -> {
						final IPAddress address = new IPAddressString(ip).getAddress();
						if (ip.contains(":")) {
							iPv6Addresses.add(address.toIPv6());
						} else {
							iPv4Addresses.add(address.toIPv4());
						}
					});
			return Pair.of(iPv4Addresses, iPv6Addresses);
		} catch (IOException e) {
			log.error("Failed to parse Private Relay IP ranges", e);
		}
		return ImmutablePair.nullPair();
	}

	/**
	 * Checks if the given ip is part of the Private Relay ranges.
	 *
	 * @param ip to check
	 * @return true if ip is part of the Private Relay ranges, false otherwise
	 */
	public boolean isFromPrivateRelay(String ip) {
		if (StringUtils.isEmpty(ip) || iPv4AddressTrie == null) {
			return false;
		}

		final IPAddress address = new IPAddressString(ip).getAddress();
		return ip.contains(":") ? iPv6AddressTrie.elementContains(address.toIPv6()) : iPv4AddressTrie.elementContains(address.toIPv4());
	}
}
