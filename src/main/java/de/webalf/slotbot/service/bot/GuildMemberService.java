package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.GuildUsersService;
import de.webalf.slotbot.service.integration.GuildDiscordService;
import de.webalf.slotbot.util.bot.DiscordRoleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alf
 * @since 30.08.25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GuildMemberService {
	private final GuildService guildService;
	private final GuildDiscordService guildDiscordService;
	private final GuildUsersService guildUsersService;

	@Async
	public void sync(long guildId) {
		final Guild guild = guildService.findExisting(guildId);
		final Long memberRole = guild.getMemberRole();
		final Long eventManageRole = guild.getEventManageRole();
		final Long adminRole = guild.getAdminRole();
		if (memberRole == null && eventManageRole == null && adminRole == null) {
			log.debug("No roles configured for guild {}, skipping sync", guild.getId());
			return;
		}
		final net.dv8tion.jda.api.entities.Guild discordGuild = guildDiscordService.getGuildById(guildId);
		final Set<Role> roles = Stream.of(memberRole, eventManageRole, adminRole)
				.filter(Objects::nonNull)
				.map(roleId -> DiscordRoleUtils.getRoleById(discordGuild, roleId))
				.collect(Collectors.toUnmodifiableSet());
		discordGuild.findMembers(member -> !Collections.disjoint(member.getUnsortedRoles(), roles))
				.onSuccess(members -> {
					Collection<Member> membersToProcess = members;
					if (adminRole == null) { //If there is no admin role, skip admins from processing
						final Set<Long> admins = guildUsersService.findByGuildAndAdmin(guild);
						membersToProcess = members.stream()
								.filter(member -> !admins.contains(member.getIdLong()))
								.collect(Collectors.toUnmodifiableSet());
					}
					final Set<Long> updatedMembers = membersToProcess.stream()
							.map(member -> {
								guildUsersService.onRolesChanged(discordGuild.getIdLong(), member.getIdLong(), DiscordRoleUtils.getRoleIds(member.getRoles()));
								return member.getIdLong();
							})
							.collect(Collectors.toUnmodifiableSet());
					guildUsersService.removeExcept(guild, updatedMembers, adminRole == null);
				});
	}
}
