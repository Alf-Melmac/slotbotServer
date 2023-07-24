package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * @author Alf
 * @since 07.07.2021
 */
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.archive",
		description = "bot.slash.event.archive.description",
		authorization = Permission.MANAGE_CHANNEL)
public class ArchiveEvent implements DiscordSlashCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent interactionEvent, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: unslot");

		final MessageChannelUnion channel = interactionEvent.getChannel();
		eventBotService.archiveEvent(channel.getIdLong(), interactionEvent.getGuild());
		channel.delete().queue();
	}
}
