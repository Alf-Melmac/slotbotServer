package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.GuildBotService;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.util.bot.ChannelUtils;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static de.webalf.slotbot.util.bot.InteractionUtils.failedInteraction;
import static de.webalf.slotbot.util.bot.InteractionUtils.reply;

/**
 * @author Alf
 * @since 07.07.2021
 */
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.rebuildArchive",
		description = "bot.slash.event.rebuildArchive.description",
		authorization = Permission.ADMINISTRATOR)
public class RebuildArchive implements DiscordSlashCommand {
	private final EventBotService eventBotService;
	private final GuildBotService guildBotService;

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: rebuildArchive");

		final Guild guild = event.getGuild();
		//noinspection DataFlowIssue Guild only command
		final Long guildArchiveChannel = guildBotService.getGuildArchiveChannel(guild.getIdLong());
		if (guildArchiveChannel == null) {
			failedInteraction(event, locale.t("bot.slash.event.rebuildArchive.response.configMissing"));
			return;
		}
		final TextChannel archiveChannel = ChannelUtils.getChannel(guildArchiveChannel, guild, "archive");
		if (archiveChannel == null) {
			failedInteraction(event, locale.t("bot.slash.event.rebuildArchive.response.channelMissing"));
			return;
		}
		eventBotService.retriggerArchiveEvents(guild);
		reply(event, archiveChannel.getAsMention());
	}
}
