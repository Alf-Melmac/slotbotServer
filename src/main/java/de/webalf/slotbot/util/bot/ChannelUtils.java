package de.webalf.slotbot.util.bot;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * Util class to work with discord channels
 *
 * @author Alf
 * @since 13.09.2021
 */
@UtilityClass
@Slf4j
public final class ChannelUtils {
	/**
	 * Returns the message channel for the given channelId in the given guild
	 *
	 * @param channelId   channel to get
	 * @param guild       in which the channel is located
	 * @param channelType information added to error output
	 * @return channel found by id or null
	 */
	public static MessageChannel getChannel(Long channelId, @NonNull Guild guild, String channelType) {
		if (channelId == null) {
			log.warn("Channel {} not configured.", channelType);
			return null;
		}
		final MessageChannel channel = guild.getChannelById(MessageChannel.class, channelId);
		if (channel == null) {
			log.error("Configured channel {} with id {} doesn't exist.", channelType, channelId);
			return null;
		}
		return channel;
	}

	/**
	 * Checks if the bot has the given permission in the given guild channel
	 *
	 * @param channel    to check effective permission in
	 * @param permission to check
	 * @return true if permission is granted
	 */
	public static boolean botHasPermission(@NonNull GuildMessageChannel channel, @NonNull Permission... permission) {
		return channel.getGuild().getSelfMember().hasPermission(channel, permission);
	}
}
