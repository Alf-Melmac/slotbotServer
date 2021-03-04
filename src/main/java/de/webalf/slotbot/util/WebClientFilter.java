package de.webalf.slotbot.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

/**
 * @author Alf
 * @since 04.03.2021
 */
@Slf4j
//TODO: Delete me
public class WebClientFilter {

	public static ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(request -> {
			logMethodAndUrl(request);
			logHeaders(request);

			return Mono.just(request);
		});
	}


	private static void logHeaders(ClientRequest request) {
		request.headers().forEach((name, values) -> values.forEach(value -> logNameAndValuePair(name, value)));
	}


	private static void logNameAndValuePair(String name, String value) {
		log.debug("{}={}", name, value);
	}


	private static void logMethodAndUrl(ClientRequest request) {
		log.debug(request.method().name() +
				" to " +
				request.url());
	}
}
