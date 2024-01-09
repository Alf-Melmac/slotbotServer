package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.service.EventDiscordInformationService;
import de.webalf.slotbot.service.bot.CommandsService;
import de.webalf.slotbot.service.bot.GuildUsersBotService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * This requires Intents
 * <ul>
 *     <li>{@link GatewayIntent#GUILD_MEMBERS} to check for added or removed roles of a member</li>
 * </ul>
 *
 * @author Alf
 * @since 15.07.2021
 */
@RequiredArgsConstructor
@Slf4j
public class GuildEventListener extends ListenerAdapter {
	private final CommandsService commandsService;
	private final EventDiscordInformationService eventDiscordInformationService;
	private final GuildUsersBotService guildUsersBotService;

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
		commandsService.updateCommands(guild);
	}

	@Override
	public void onGuildMemberRoleAdd(@NonNull GuildMemberRoleAddEvent event) {
		final Member member = event.getMember();
		guildUsersBotService.memberRolesAdd(event.getGuild().getIdLong(), member.getIdLong(), event.getRoles(), member.getRoles());
	}

	@Override
	public void onGuildMemberRoleRemove(@NonNull GuildMemberRoleRemoveEvent event) {
		final Member member = event.getMember();
		guildUsersBotService.memberRolesRemove(event.getGuild().getIdLong(), member.getIdLong(), event.getRoles(), member.getRoles());
	}

	@Override
	public void onGuildLeave(@NonNull GuildLeaveEvent event) {
		final Guild guild = event.getGuild();
		log.info("Cleanup for guild: {}", guild.getName());
		eventDiscordInformationService.removeByGuild(guild.getIdLong());
	}

	@Override
	public void onGuildMemberRemove(@NonNull GuildMemberRemoveEvent event) {
		guildUsersBotService.remove(event.getGuild().getIdLong(), event.getUser().getIdLong());
	}
}
