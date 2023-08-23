package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.bot.ButtonInteraction;
import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.model.bot.TranslatableOptionData;
import de.webalf.slotbot.model.enums.SwapRequestResult;
import de.webalf.slotbot.service.bot.SwapRequestBotService;
import de.webalf.slotbot.service.bot.command.DiscordButton;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.util.bot.ButtonUtils;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Service;

import java.util.List;

import static de.webalf.slotbot.util.bot.InteractionUtils.finishedInteraction;
import static de.webalf.slotbot.util.bot.InteractionUtils.reply;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getUserOption;

/**
 * @author Alf
 * @since 13.01.2021
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.swap",
		description = "bot.slash.event.swap.description",
		authorization = Permission.MESSAGE_HISTORY,
		optionPosition = 0)
@ButtonInteraction(Swap.SWAP_ACCEPT)
@ButtonInteraction(Swap.SWAP_DECLINE)
public class Swap implements DiscordSlashCommand, DiscordButton {
	private final SwapRequestBotService swapRequestBotService;

	public static final String SWAP_DECLINE = "swap_decline";
	public static final String SWAP_ACCEPT = "swap_accept";

	private static final String OPTION_SWAP_USER = "bot.slash.event.swap.option.user";
	private static final List<List<TranslatableOptionData>> OPTIONS = List.of(
			List.of(new TranslatableOptionData(OptionType.USER, OPTION_SWAP_USER, "bot.slash.event.swap.option.user.description", true))
	);

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: swap");

		final long userId = getUserOption(event, OPTION_SWAP_USER);

		final SwapRequestResult swapRequestResult = swapRequestBotService.swapByUsers(event.getChannel().getIdLong(), event.getUser().getIdLong(), userId);
		switch (swapRequestResult) {
			case ERROR_OWN_SLOT -> reply(event, locale.t("bot.slash.event.swap.response.ownSlot"));
			case ERROR_PENDING -> reply(event, locale.t("bot.slash.event.swap.response.pending"));
			default -> finishedInteraction(event);
		}
	}

	@Override
	public List<TranslatableOptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}

	@Override
	public void handle(@NonNull ButtonInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Button: swap");

		final String[] split = ButtonUtils.splitButtonId(event);
		final String interaction = split[0];
		final long swapRequestId = Long.parseLong(split[1]);

		if (SWAP_ACCEPT.equals(interaction)) {
			swapRequestBotService.accept(swapRequestId);
		} else if (SWAP_DECLINE.equals(interaction)) {
			swapRequestBotService.decline(swapRequestId);
		} else {
			throw new IllegalStateException("Unknown swap button: " + event.getButton().getId());
		}
	}
}
