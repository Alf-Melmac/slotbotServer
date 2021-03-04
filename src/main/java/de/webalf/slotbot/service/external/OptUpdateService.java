package de.webalf.slotbot.service.external;

import com.google.auth.oauth2.GoogleCredentials;
import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.configuration.properties.OptProperties;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.api.EventApiViewDto;
import de.webalf.slotbot.util.StringUtils;
import de.webalf.slotbot.util.WebClientFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Alf
 * @since 04.03.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class OptUpdateService {
	private final OptProperties optProperties;
	private final EventApiAssembler eventApiAssembler;

	private Boolean configured = null;

	public void sendUpdate(Event event) {
		if (!configured()) {
			return;
		}

		final String token = getGoogleCredentialToken();

		final WebClient webClient = buildWebClient(token);
		log.warn(webClient.get().retrieve().bodyToMono(String.class).block()); //GET WORKS

		final EventApiViewDto eventApiViewDto = eventApiAssembler.toViewDto(event);
		String block = webClient.put().body(Mono.just(eventApiViewDto), EventApiViewDto.class).retrieve() //FIXME
				.onStatus(HttpStatus::isError, clientResponse -> {
					throw BusinessRuntimeException.builder().description(clientResponse.statusCode().getReasonPhrase()).build();
				})
				.bodyToMono(String.class)
				.block();

		System.out.println(block);
	}

	/**
	 * @return Access token fetched from the configured service account json file
	 */
	private String getGoogleCredentialToken() {
		GoogleCredentials credentials;
		// Authenticate a Google credential with the service account
		try (FileInputStream credentialsStream = new FileInputStream(optProperties.getGoogleCredentialsLocation())) {
			credentials = GoogleCredentials.fromStream(credentialsStream);
		} catch (FileNotFoundException e) {
			log.error("Credentials not found.");
			return null;
		} catch (IOException e) {
			log.error("Couldn't acquire credentials from given file.");
			return null;
		}

		// Add the required scopes to the Google credential
		final GoogleCredentials scoped = credentials.createScoped(
				Arrays.asList(
						"https://www.googleapis.com/auth/firebase.database",
						"https://www.googleapis.com/auth/userinfo.email"
				)
		);

		// Use the Google credential to generate an access token
		try {
			scoped.refreshIfExpired();
		} catch (IOException e) {
			log.error("Credential refresh failed.");
			return null;
		}
		return scoped.getAccessToken().getTokenValue();
	}

	private WebClient buildWebClient(String token) {
		return WebClient.builder()
				.baseUrl("https://opt-stats.firebaseio.com/ambParticipants.json")
//				.baseUrl("https://opt-stats.firebaseio.com/rest/ambParticipants.json")
				.filter(WebClientFilter.logRequest())
				.defaultHeader("access_token", token)
				.build();
	}

	/**
	 * @return true if the credential location has been defined. Will be evaluated once per runtime
	 */
	private boolean configured() {
		if (configured == null) {
			configured = StringUtils.isNotEmpty(optProperties.getGoogleCredentialsLocation());
		}
		return configured;
	}
}
