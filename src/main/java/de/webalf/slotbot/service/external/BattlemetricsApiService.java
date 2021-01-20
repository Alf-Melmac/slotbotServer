package de.webalf.slotbot.service.external;

import de.webalf.slotbot.configuration.properties.BattlemetricsProperties;
import de.webalf.slotbot.model.external.ServerStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alf
 * @since 19.01.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class BattlemetricsApiService {
	private final BattlemetricsProperties apiProperties;

	/**
	 * Fetches all configured server from the battlemetrics api
	 *
	 * @return Set of {@link Server}s acquired from the api
	 */
	public Set<Server> getServers() {
		Set<Server> servers = new HashSet<>();

		for (int serverId : apiProperties.getServerIds()) {
			final String url = "/servers/" + serverId + "?fields[server]=name,ip,port,status";

			Response response = buildWebClient().get().uri(url).retrieve().bodyToMono(Response.class).block();
			if (response == null || response.getData() == null || response.getData().getAttributes() == null) {
				log.warn("Server with id " + serverId + " couldn't be reached via battlemetrics api.");
				continue;
			}
			servers.add(response.getData().getAttributes());
		}

		return servers;
	}

	private WebClient buildWebClient() {
		return WebClient.builder()
				.baseUrl("https://api.battlemetrics.com")
				.defaultHeader("Authorization", apiProperties.getApiAccessToken())
				.build();
	}

	@Data
	private static class Response {
		private Identifier data;
	}

	@Data
	private static class Identifier {
		private String type;
		private int id;
		private Server attributes;
	}

	@Data
	public static class Server {
		private String name;
		private String ip;
		private int port;
		private ServerStatus status;

		public String getFullIp() {
			return getIp() + ":" + getPort();
		}
	}
}
