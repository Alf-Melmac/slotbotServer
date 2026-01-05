package de.webalf.slotbot.util.bot;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildMessageChannelMixin;

import java.time.Instant;

import static net.dv8tion.jda.api.Permission.MESSAGE_MANAGE;
import static net.dv8tion.jda.api.Permission.PIN_MESSAGES;

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
	 * Returns the text channel for the given channelId in the given guild
	 *
	 * @param channelId   channel to get
	 * @param guild       in which the channel is located
	 * @param channelType information added to error output
	 * @return channel found by id or null
	 */
	public static TextChannel getChannel(Long channelId, @NonNull Guild guild, String channelType) {
		if (channelId == null) {
			log.warn("Channel {} not configured.", channelType);
			return null;
		}
		final TextChannel channel = guild.getTextChannelById(channelId);
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

	private static final Instant PIN_PERMISSION_DEADLINE = Instant.parse("2026-02-23T00:00:00Z");

	/**
	 * Until {@link #PIN_PERMISSION_DEADLINE} Discord allows pinning with {@link Permission#MESSAGE_MANAGE}, afterwards only with {@link Permission#PIN_MESSAGES}
	 *
	 * @see GuildMessageChannelMixin#checkCanControlMessagePins()
	 */
	public static boolean botHasPermissionMessagePins(@NonNull GuildMessageChannel channel) {
		if (Instant.now().isBefore(PIN_PERMISSION_DEADLINE) && botHasPermission(channel, MESSAGE_MANAGE)) {
			return true;
		}
		return botHasPermission(channel, PIN_MESSAGES);
	}
}
