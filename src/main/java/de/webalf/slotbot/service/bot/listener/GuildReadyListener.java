package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.service.bot.CommandsService;
import de.webalf.slotbot.util.bot.RoleUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Alf
 * @since 15.07.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuildReadyListener extends ListenerAdapter {
	private final CommandsService commandsService;

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event) {
		final Guild guild = event.getGuild();
		RoleUtils.checkRequiredRoles(guild);
		commandsService.updateCommands(guild);
	}
}
