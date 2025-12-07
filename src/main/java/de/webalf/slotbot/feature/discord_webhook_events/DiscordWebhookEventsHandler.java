package de.webalf.slotbot.feature.discord_webhook_events;

import de.webalf.slotbot.model.external.discord.DiscordGuild;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.GuildUsersService;
import de.webalf.slotbot.util.permissions.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * @author Alf
 * @since 07.11.2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiscordWebhookEventsHandler {
	private final ObjectMapper objectMapper = JsonMapper.builder()
			.enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
			.build();
	private final GuildUsersService guildUsersService;
	private final GuildService guildService;

	@Async
	void handle(String body) throws JacksonException {
		final UnclassifiedDiscordWebhookEventPayload payload = objectMapper.readValue(body, UnclassifiedDiscordWebhookEventPayload.class);
		if (payload.type() != WebhookType.EVENT) { // Ignore non-event payloads
			return;
		}

		// Handle supported event types
		if (payload.event().type() == EventType.APPLICATION_AUTHORIZED) {
			log.trace("Received application authorized event: {}", body);
			final ApplicationAuthorizedEvent applicationAuthorizedEvent = objectMapper.readValue(body, ApplicationAuthorizedEvent.class);
			final ApplicationAuthorizedEvent.EventBody.ApplicationAuthorizedData data = applicationAuthorizedEvent.event().data();
			if (!Integer.valueOf(0).equals(data.integration_type())) {
				// Ignore non guild install events
				return;
			}

			final DiscordGuild guild = data.guild();
			if (guild == null) {
				log.warn("Received application authorized event without guild: {}", body);
				return;
			}
			guildService.create(guild.id(), guild.name());
			guildUsersService.add(guild.id(), data.user().id(), Role.ADMINISTRATOR);
		}
	}
}
