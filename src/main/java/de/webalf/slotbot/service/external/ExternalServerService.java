package de.webalf.slotbot.service.external;

import de.webalf.slotbot.configuration.properties.ServerManagerProperties;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alf
 * @since 24.01.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ExternalServerService {
	private final ServerManagerProperties serverManagerProperties;

	private Map<String, String> ipServerMap = new HashMap<>();

	/**
	 * Checks if the api of the server manager is reachable
	 *
	 * @return status returned by the ping request or null if not reachable
	 */
	public HttpStatus ping() {
		HttpStatus status = null;
		try {
			status = buildWebClient().get().uri("/status")
					.exchangeToMono(response -> Mono.just(response.statusCode())).block();
		} catch (Exception ignored) {
		}
		return status;
	}

	/**
	 * Fills the ipUrlMap "cache" with the mappings from the server manager
	 */
	public void fillIpServerMap() {
		log.info("Filling ipUrlMap from " + serverManagerProperties.getUrl());
		ipServerMap = buildWebClient().get().uri("/status/mappings").retrieve()
				.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
				.onErrorReturn(Collections.emptyMap())
				.block();
		log.info("Filled ipUrlMap and retrieved {} items", ipServerMap != null ? ipServerMap.size() : "null");
	}

	public void restartServer(@NonNull BattlemetricsApiService.Identifier identifier) {
		final String fullIp = identifier.getAttributes().getFullIp();
		if (identifier.isServerEmpty() && knownServer(fullIp)) {
			buildWebClient().put().uri("/" + ipServerMap.get(fullIp)).retrieve()
					.onStatus(HttpStatus::isError, clientResponse -> {
						throw BusinessRuntimeException.builder().description(clientResponse.statusCode().getReasonPhrase()).build();
					})
					.bodyToMono(Boolean.class).block();
		} else {
			throw BusinessRuntimeException.builder().title("Server must be empty and known by the server manager.").build();
		}
	}

	/**
	 * Checks if the given server matched by ip is a known external server
	 *
	 * @param fullIp to check
	 * @return true if the external server has a mapping for the given ip with port
	 */
	boolean knownServer(String fullIp) {
		return ipServerMap.containsKey(fullIp);
	}

	private WebClient buildWebClient() {
		return WebClient.builder()
				.baseUrl(serverManagerProperties.getUrl())
				.defaultHeader(serverManagerProperties.getTokenName(), serverManagerProperties.getToken())
				.build();
	}
}
