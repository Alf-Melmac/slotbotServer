package de.webalf.slotbot.feature.discord_webhook_events;

/**
 * Webhook event payload before it is known which event body type it contains
 *
 * @author Alf
 * @see <a href="https://discord.com/developers/docs/events/webhook-events#webhook-event-payloads">Webhook Event Payload</a>
 * @since 04.11.2024
 */
record UnclassifiedDiscordWebhookEventPayload(
		int version,
		String application_id,
		WebhookType type,
		EventClassifier event
) {
	record EventClassifier(EventType type) {}
}
