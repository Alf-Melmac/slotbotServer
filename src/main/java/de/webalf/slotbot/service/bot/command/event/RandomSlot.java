package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.exception.SlottableException;
import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static de.webalf.slotbot.util.bot.InteractionUtils.finishedVisibleInteraction;
import static de.webalf.slotbot.util.bot.InteractionUtils.reply;

/**
 * @author Alf
 * @since 31.03.2021
 */
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.randomSlot",
		description = "bot.slash.event.randomSlot.description",
		authorization = Permission.MESSAGE_HISTORY)
public class RandomSlot implements DiscordSlashCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: randomSlot");

		try {
			eventBotService.randomSlot(event.getChannel().getIdLong(), event.getUser().getId());
		} catch (SlottableException e) {
			reply(event, locale.t(e.getSlottable().state().getMessageKey()));
			return;
		}

		finishedVisibleInteraction(event);
	}
}
