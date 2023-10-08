package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * Utility class to work with {@link RestTemplate}s
 *
 * @author Alf
 * @since 12.06.2021
 */
@UtilityClass
public final class RestTemplatesUtil {
	public static <T> ResponseEntity<T> get(String url, String authorization, ResponseErrorHandler errorHandler, Class<T> clazz) {
		return new RestTemplateBuilder().
				defaultHeader(HttpHeaders.AUTHORIZATION, authorization)
				.errorHandler(errorHandler)
				.build()

				.getForEntity(url, clazz);
	}
}
