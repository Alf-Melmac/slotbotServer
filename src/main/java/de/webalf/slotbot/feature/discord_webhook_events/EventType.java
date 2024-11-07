package de.webalf.slotbot.feature.discord_webhook_events;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.AllArgsConstructor;

/**
 * @author Alf
 * @see <a href="https://discord.com/developers/docs/events/webhook-events#event-types">Event Types</a>
 * @since 07.11.2024
 */
@AllArgsConstructor
enum EventType {
	/**
	 * Sent when an app was authorized by a user to a server or their account
	 */
	APPLICATION_AUTHORIZED,
	/**
	 * Unsupported event type
	 */
	@JsonEnumDefaultValue
	UNSUPPORTED
}
