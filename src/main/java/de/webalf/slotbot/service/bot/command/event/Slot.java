package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.exception.SlottableException;
import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.feature.slot_rules.Slottable;
import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.model.annotations.bot.StringSelectInteraction;
import de.webalf.slotbot.model.bot.TranslatableOptionData;
import de.webalf.slotbot.model.enums.SlottableState;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.RequirementBotService;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.service.bot.command.DiscordStringSelect;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.webalf.slotbot.model.enums.SlottableState.NO_REQUIREMENTS_NOT_MET;
import static de.webalf.slotbot.model.enums.SlottableState.YES_REQUIREMENTS_NOT_MET;
import static de.webalf.slotbot.util.bot.InteractionUtils.*;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getIntegerOption;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getOptionalUserOption;

/**
 * @author Alf
 * @since 10.01.2021
 */
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.slot",
		description = "bot.slash.event.slot.description",
		authorization = Permission.MESSAGE_HISTORY,
		optionPosition = 0)
@StringSelectInteraction("addFulfilledRequirement")
@SlashCommand(name = "bot.slash.event.slot.force",
		description = "bot.slash.event.slot.force.description",
		authorization = Permission.MANAGE_CHANNEL,
		optionPosition = 1)
public class Slot implements DiscordSlashCommand, DiscordStringSelect {
	private final EventBotService eventBotService;
	private final RequirementBotService requirementBotService;

	private static final String OPTION_SLOT_NUMBER = "bot.slash.event.slot.option.number";
	private static final String OPTION_SLOT_USER = "bot.slash.event.slot.option.user";
	private static final List<List<TranslatableOptionData>> OPTIONS = List.of(
			List.of(new TranslatableOptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "bot.slash.event.slot.option.number.description", true)),
			List.of(new TranslatableOptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "bot.slash.event.slot.option.number.description", true),
					new TranslatableOptionData(OptionType.USER, OPTION_SLOT_USER, "bot.slash.event.slot.option.user.description", true))
	);

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: slot");

		final int slotNumber = getIntegerOption(event, OPTION_SLOT_NUMBER);

		final Long user = getOptionalUserOption(event, OPTION_SLOT_USER);
		Set<Requirement> requirementsNotMet = null;
		try {
			if (user == null) { //Self slot
				final Slottable slottable = eventBotService.getSlottable(event.getChannel().getIdLong(), slotNumber, event.getUser().getIdLong());
				requirementsNotMet = slottable.requirementsNotMet();

				eventBotService.slot(event.getChannel().getIdLong(), slotNumber, event.getUser().getIdLong());
			} else { //Slot others
				eventBotService.slot(event.getChannel().getIdLong(), slotNumber, user);
			}
		} catch (SlottableException e) {
			handleSlottableException(event, locale, e, requirementsNotMet);
			return;
		}

		if (!CollectionUtils.isEmpty(requirementsNotMet)) { //Only gets filled if selfSlot
			addSelectMenu(event,
					locale.t(YES_REQUIREMENTS_NOT_MET.getMessageKey())
							+ "\n"
							+ locale.t("slottableState.requirementsNotMet.memberAssignable"),
					requirementsSelectMenu(requirementsNotMet));
			return;
		}
		finishedVisibleInteraction(event);
	}

	private void handleSlottableException(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale, @NonNull SlottableException e, Set<Requirement> requirementsNotMet) {
		final SlottableState slottableState = e.getSlottable().state();
		final String slottableReply = locale.t(slottableState.getMessageKey());
		if (NO_REQUIREMENTS_NOT_MET.equals(slottableState) && requirementsNotMet != null) {
			final Map<Boolean, List<Requirement>> requirementByMemberAssignable = requirementsNotMet.stream()
					.collect(Collectors.partitioningBy(r -> r.getRequirementList().isMemberAssignable()));
			final List<Requirement> memberAssignable = requirementByMemberAssignable.get(true);
			final List<Requirement> notMemberAssignable = requirementByMemberAssignable.get(false);

			String reply = "**" + slottableReply + "** " + locale.t("slottableState.required");
			if (!notMemberAssignable.isEmpty()) {
				reply += "\n"
						+ locale.t("slottableState.requirementsNotMet.notMemberAssignable")
						+ " **"
						+ notMemberAssignable.stream().map(requirement ->
								requirement.getName() + (requirement.getRequirementList().isEnforced() ? "\\*" : ""))
						.collect(Collectors.joining(", "))
						+ "**";
			}
			if (!memberAssignable.isEmpty()) {
				reply += "\n" + locale.t("slottableState.requirementsNotMet.memberAssignable");
				addSelectMenu(event, reply, requirementsSelectMenu(memberAssignable));
				return;
			}

			reply(event, reply);
		} else {
			reply(event, slottableReply);
		}
	}

	private StringSelectMenu requirementsSelectMenu(@NonNull Iterable<Requirement> requirements) {
		final String menuId = getClass().getAnnotation(StringSelectInteraction.class).value()[0];
		final StringSelectMenu.Builder selectMenuBuilder = StringSelectMenu.create(menuId)
				.setPlaceholder("Voraussetzungen auswählen")
				.setMaxValues((int) requirements.spliterator().getExactSizeIfKnown());

		for (Requirement requirement : requirements) {
			selectMenuBuilder.addOption(
					requirement.getName() + (requirement.getRequirementList().isEnforced() ? "*" : "") + " (" + requirement.getRequirementList().getName() + ")",
					Long.toString(requirement.getId()));
		}

		return selectMenuBuilder.build();
	}

	@Override
	public List<TranslatableOptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}

	@Override
	public void process(@NonNull StringSelectInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Selection menu: addFulfilledRequirement");

		event.getValues().forEach(requirementId -> requirementBotService
				.fulfillRequirement(Long.parseLong(requirementId), event.getUser().getIdLong()));
	}
}
