package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static de.webalf.slotbot.util.bot.MessageUtils.sendMessage;

/**
 * @author Alf
 * @since 09.09.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class InviteService {
	private final DiscordProperties discordProperties;

	private static final Map<Long, List<Invite>> GUILD_INVITES_MAP = new HashMap<>();

	/**
	 * Fills the invite cache for the given guild
	 *
	 * @param guild to save invites for
	 */
	private static void fillInvitesMap(@NonNull Guild guild) {
		guild.retrieveInvites().queue(invites -> GUILD_INVITES_MAP.put(guild.getIdLong(), new ArrayList<>(invites)));
	}

	/**
	 * @see #fillInvitesMap(Guild)
	 */
	public static void initialize(@NonNull Guild guild) {
		log.info("Initializing invite cache for {}", guild.getName());
		fillInvitesMap(guild);
	}

	/**
	 * Saves a new invite to the cache
	 *
	 * @param guildId in which the invite was created
	 * @param invite  created invite
	 */
	public void newInvite(long guildId, Invite invite) {
		final List<Invite> invites = new ArrayList<>(GUILD_INVITES_MAP.get(guildId)); //Just to be sure it's modifiable
		invites.add(invite);
		GUILD_INVITES_MAP.put(guildId, invites);
	}

	/**
	 * Refreshes the invite cache for the given guild
	 *
	 * @param guild to refresh invites for
	 */
	public void deletedInvite(Guild guild) {
		fillInvitesMap(guild);
	}

	/**
	 * Posts a message to the {@link DiscordProperties#modLog} channel and tries to append the used invite link
	 *
	 * @param guild  joined into this guild
	 * @param member joined member
	 */
	public void memberJoined(@NonNull Guild guild, Member member) {
		guild.retrieveInvites().queue(invites -> {
			final TextChannel modLogChannel = getModLogChannel(guild);
			if (modLogChannel == null) return;

			final List<Invite> oldInvites = GUILD_INVITES_MAP.get(guild.getIdLong());

			boolean found = false;
			if (invites.size() == oldInvites.size()) {
				for (Invite invite : invites) {
					final Optional<Invite> optionalInvite = oldInvites.stream()
							.filter(oldInvite -> oldInvite.getCode().equals(invite.getCode()) && oldInvite.getUses() != invite.getUses())
							.findAny();
					if (optionalInvite.isPresent()) {
						sendLogMessage(member, modLogChannel, optionalInvite.get());
						found = true;
						break;
					}
				}
			} else { //Link is now invalid
				oldInvites.removeAll(invites);
				for (Invite oldInvite : oldInvites) {
					sendLogMessage(member, modLogChannel, oldInvite);
					found = true;
				}
			}
			if (!found) {
				sendMessage(modLogChannel, ":warning: " + member.getAsMention() + " ist beigetreten, es konnte allerdings keine Einladung ermittelt werden.");
			}
			GUILD_INVITES_MAP.put(guild.getIdLong(), invites);
		});
	}

	/**
	 * Returns the configured mod log channel for the given guild
	 *
	 * @param guild to get channel for
	 * @return mod log channel or null
	 */
	private TextChannel getModLogChannel(@NonNull Guild guild) {
		final Long modLogChannelId = discordProperties.getModLog();
		if (modLogChannelId == null) {
			log.warn("Mod log channel not configured.");
			return null;
		}
		final TextChannel modLogChannel = guild.getJDA().getTextChannelById(modLogChannelId);
		if (modLogChannel == null) {
			log.error("Configured Mod log channel doesn't exist.");
			return null;
		}
		return modLogChannel;
	}

	private void sendLogMessage(@NonNull Member member, @NonNull TextChannel modLogChannel, @NonNull Invite oldInvite) {
		sendMessage(modLogChannel, member.getAsMention() + " ist beigetreten und hat die Einladung **" + oldInvite.getCode() + "** verwendet.");
	}
}
