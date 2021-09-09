package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.service.bot.InviteService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Alf
 * @since 09.09.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuildInviteListener extends ListenerAdapter {
	private final InviteService inviteService;

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
		inviteService.memberJoined(event.getGuild(), event.getMember());
	}

	@Override
	public void onGuildInviteCreate(@NotNull GuildInviteCreateEvent event) {
		inviteService.newInvite(event.getGuild().getIdLong(), event.getInvite());
	}

	@Override
	public void onGuildInviteDelete(@NotNull GuildInviteDeleteEvent event) {
		inviteService.deletedInvite(event.getGuild());
	}
}
