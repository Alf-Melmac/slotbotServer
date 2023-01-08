package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.bot.ChannelUtils;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static de.webalf.slotbot.util.bot.InteractionUtils.failedInteraction;
import static de.webalf.slotbot.util.bot.InteractionUtils.reply;
import static de.webalf.slotbot.util.bot.MessageUtils.sendMessage;

/**
 * @author Alf
 * @since 07.07.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@SlashCommand(name = "bot.slash.event.rebuildArchive",
		description = "bot.slash.event.rebuildArchive.description",
		authorization = Permission.ADMINISTRATOR)
public class RebuildArchive implements DiscordSlashCommand {
	private final EventBotService eventBotService;
	private final DiscordProperties discordProperties;

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: rebuildArchive");

		final Guild guild = event.getGuild();
		//noinspection DataFlowIssue Guild only command
		final TextChannel archiveChannel = ChannelUtils.getChannel(discordProperties.getArchive(guild.getIdLong()), guild, "archive");
		if (archiveChannel == null) {
			failedInteraction(event, locale.t("bot.slash.event.archive.response.configError"));
			return;
		}

		eventBotService.findAllInPast().forEach(pastEvent -> sendMessage(archiveChannel, EventUtils.buildArchiveMessage(pastEvent)));
		reply(event, archiveChannel.getAsMention());
	}
}
