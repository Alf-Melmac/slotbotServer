package de.webalf.slotbot.feature.discord_webhook_events;

import de.webalf.slotbot.model.external.discord.DiscordGuild;
import de.webalf.slotbot.model.external.discord.DiscordUser;

/**
 * @author Alf
 * @see <a href="https://discord.com/developers/docs/events/webhook-events#application-authorized">Application Authorized</a>
 * @since 07.11.2024
 */
record ApplicationAuthorizedEvent(
		int version,
		String application_id,
		WebhookType type,
		EventBody event
) {
	record EventBody(String type, String timestamp, ApplicationAuthorizedData data) {
		record ApplicationAuthorizedData(int integration_type, DiscordUser user, String[] scopes, DiscordGuild guild) {}
	}
}
