package de.webalf.slotbot.service.external;

import de.webalf.slotbot.configuration.properties.BattlemetricsProperties;
import de.webalf.slotbot.model.external.ServerStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

	private Map<String, Identifier> identifierCache = new HashMap<>();

	/**
	 * Fetches all configured server from the battlemetrics api
	 *
	 * @return Set of {@link Server}s acquired from the api
	 */
	public Set<Server> getServers() {
		final Set<Server> servers = new HashSet<>();
		identifierCache = new HashMap<>();

		for (int serverId : apiProperties.getServerIds()) {
			final String url = "/servers/" + serverId + "?fields[server]=name,ip,port,players,status";

			final Response response = buildWebClient().get().uri(url).retrieve().bodyToMono(Response.class).block();
			if (response == null) {
				log.warn("Server with id " + serverId + " couldn't be reached via battlemetrics api.");
				continue;
			}
			final Server server = response.getServer();
			servers.add(server);
			identifierCache.put(server.getFullIp(), response.getData());
		}

		return servers;
	}

	/**
	 * Returns the matching {@link Identifier} for the given ip.
	 * May only be called after {@link #getServers()} with a ip returned from there.
	 *
	 * @param fullIp ip to search identifier for
	 * @return matching identifier or null
	 */
	public Identifier findIdentifierByFullIp(String fullIp) {
		return identifierCache.get(fullIp);
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

		private Server getServer() {
			final Server server = getData().getAttributes();
			server.setArma(getData().getRelationships().isArma());
			return server;
		}
	}

	@Data
	static class Identifier {
		private String type;
		private int id;
		private Server attributes;
		private Relationships relationships;

		/**
		 * @return true if the player count is zero
		 */
		boolean isServerEmpty() {
			return getAttributes().getPlayers() == 0;
		}
	}

	@Data
	public static class Server {
		private String name;
		private String ip;
		private int port;
		private int players;
		private ServerStatus status;

		private boolean isArma = false;

		/**
		 * @return ip and port concatenated with :
		 */
		public String getFullIp() {
			return getIp() + ":" + getPort();
		}
	}

	@Data
	static class Relationships {
		private Game game;

		/**
		 * Checks if the game id is 'arma3'
		 *
		 * @return true if the id matches
		 */
		boolean isArma() {
			return getGame().getData().getId().equals("arma3");
		}
	}

	@Data
	private static class Game {
		private GameData data;
	}

	@Data
	private static class GameData {
		private String type;
		private String id;
	}
}
