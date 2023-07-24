package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.service.EventDiscordInformationService;
import de.webalf.slotbot.service.bot.CommandsService;
import de.webalf.slotbot.util.bot.RoleUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @author Alf
 * @since 15.07.2021
 */
@RequiredArgsConstructor
@Slf4j
public class GuildReadyListener extends ListenerAdapter {
	private final CommandsService commandsService;
	private final EventDiscordInformationService eventDiscordInformationService;

	@Override
	public void onGuildReady(@NonNull GuildReadyEvent event) {
		initializeGuild(event);
	}

	@Override
	public void onGuildAvailable(@NonNull GuildAvailableEvent event) {
		initializeGuild(event);
	}

	@Override
	public void onGuildJoin(@NonNull GuildJoinEvent event) {
		initializeGuild(event);
	}

	private void initializeGuild(@NonNull GenericGuildEvent event) {
		final Guild guild = event.getGuild();
		log.info("Initializing guild: {}", guild.getName());
		RoleUtils.checkRequiredRoles(guild);
		commandsService.updateCommands(guild);
	}

	@Override
	public void onGuildLeave(@NonNull GuildLeaveEvent event) {
		final Guild guild = event.getGuild();
		log.info("Cleanup for guild: {}", guild.getName());
		eventDiscordInformationService.removeByGuild(guild.getIdLong());
	}
}
