package de.webalf.slotbot.feature.discord_webhook_events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Alf
 * @see <a href="https://discord.com/developers/docs/events/webhook-events#webhook-types">Webhook Types</a>
 * @since 07.11.2024
 */
@AllArgsConstructor
enum WebhookType {
	/**
	 * PING event sent to verify your Webhook Event URL is active
	 */
	PING(0),
	/**
	 * Webhook event (details for event in {@link UnclassifiedDiscordWebhookEventPayload#event() event body} object)
	 */
	EVENT(1),
	/**
	 * Discord added a new type which isn't known yet
	 */
	UNKNOWN(-1);

	@Getter(onMethod_ = @JsonValue)
	private final int value;

	@JsonCreator
	static WebhookType fromValue(int value) {
		for (WebhookType type : values()) {
			if (type.value == value) {
				return type;
			}
		}
		return UNKNOWN;
	}
}
