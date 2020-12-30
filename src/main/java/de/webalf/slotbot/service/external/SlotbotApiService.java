package de.webalf.slotbot.service.external;

import de.webalf.slotbot.configuration.properties.SlotbotApiProperties;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Alf
 * @since 29.12.2020
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlotbotApiService {
	private final SlotbotApiProperties slotbotApiProperties;

	/**
	 * Checks if the api of the slotbot is reachable
	 *
	 * @return status returned by the ping request or null if not reachable
	 */
	public HttpStatus ping() {
		HttpStatus status = null;
		try {
			status = buildWebClient().get().uri("/status").exchange()
					.map(ClientResponse::statusCode).block();
		} catch (Exception ignored) {
		}
		return status;
	}

	/**
	 * Informs the slotbot about an update in one event
	 *
	 * @param entity that may be an event related object
	 */
	public void update(Object entity) {
		final Event event = getEvent(entity);
		if (event == null || !event.isPrinted()) {
			return;
		}

		//TODO
//		buildWebClient().put().uri("/event/update");
	}

	/**
	 * Returns the associated event if the entity is a {@link Event}, {@link Squad} or {@link Slot}
	 *
	 * @param entity to get the event for
	 * @return associated event or null
	 */
	private Event getEvent(Object entity) {
		if (entity instanceof Event) {
			return (Event) entity;
		} else if (entity instanceof Squad) {
			final Squad squad = (Squad) entity;
			if (!squad.isReserve()) {
				return squad.getEvent();
			}
		} else if (entity instanceof Slot) {
			final Slot slot = (Slot) entity;
			if (!slot.isInReserve()) {
				return slot.getSquad().getEvent();
			}
		}
		return null;
	}

	private WebClient buildWebClient() {
		return WebClient.builder()
				.baseUrl(slotbotApiProperties.getUrl())
				.defaultHeader(slotbotApiProperties.getName(), slotbotApiProperties.getToken())
				.build();
	}
}
