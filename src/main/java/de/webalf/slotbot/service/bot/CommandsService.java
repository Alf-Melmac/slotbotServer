package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.annotations.bot.ContextMenu;
import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.model.bot.TranslatableOptionData;
import de.webalf.slotbot.util.bot.CommandClassHelper;
import de.webalf.slotbot.util.bot.ContextMenuUtils;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import de.webalf.slotbot.util.bot.SlashCommandUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.bot.CommandClassHelper.getContextMenu;
import static de.webalf.slotbot.util.bot.CommandClassHelper.getSlashCommand;
import static de.webalf.slotbot.util.bot.DiscordLocaleHelper.DEFAULT_LOCALE;
import static net.dv8tion.jda.api.interactions.DiscordLocale.GERMAN;

/**
 * @author Alf
 * @since 15.07.2021
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommandsService {
	private final CommandClassHelper commandClassHelper;
	private final MessageSource messageSource;

	private static final Set<DiscordLocale> LANGUAGES = Set.of(GERMAN);

	/**
	 * Updates application commands in the given guild
	 *
	 * @param guild to update commands for
	 */
	public void updateCommands(@NonNull Guild guild) {
		log.info("Updating commands for {}...", guild.getName());

		final Map<DiscordLocale, DiscordLocaleHelper> locales = LANGUAGES.stream()
				.collect(Collectors.toMap(Function.identity(), language -> new DiscordLocaleHelper(language, messageSource)));
		log.info("Translating to languages {}.", locales.keySet());

		final List<SlashCommandData> slashCommands = SlashCommandUtils.get().stream()
				.distinct()
				.flatMap(slashCommandClass -> {
					final SlashCommand[] slashCommandAnnotations = getSlashCommand(slashCommandClass);
					return Arrays.stream(slashCommandAnnotations).map(slashCommand -> {
						final SlashCommandData commandData = Commands
								.slash(DEFAULT_LOCALE.t(slashCommand.name()).toLowerCase(), DEFAULT_LOCALE.t(slashCommand.description()))
								.setDefaultPermissions(DefaultMemberPermissions.enabledFor(slashCommand.authorization()));
						locales.forEach((language, locale) -> commandData
								.setNameLocalization(language, locale.t(slashCommand.name()).toLowerCase())
								.setDescriptionLocalization(language, locale.t(slashCommand.description())));

						if (slashCommand.optionPosition() >= 0) { //Add options if present
							commandData.addOptions(getOptions(slashCommandClass, slashCommand.optionPosition(), locales));
						}

						return commandData;
					});
				}).toList();
		log.info("Found {} commands.", slashCommands.size());

		final List<CommandData> contextMenus = ContextMenuUtils.commandToClassMap.values().stream()
				.map(contextMenuClass -> {
					final ContextMenu contextMenu = getContextMenu(contextMenuClass);
					final CommandData commandData = Commands
							.context(contextMenu.type(), DEFAULT_LOCALE.t(contextMenu.name()))
							.setDefaultPermissions(DefaultMemberPermissions.enabledFor(contextMenu.authorization()));
					locales.forEach((language, locale) -> commandData
							.setNameLocalization(language, locale.t(contextMenu.name())));
					return commandData;
				}).toList();
		log.info("Found {} context menus.", contextMenus.size());

		guild.updateCommands().addCommands(slashCommands).addCommands(contextMenus).queue();
		log.info("Queued command update for {}.", guild.getName());
	}

	private List<OptionData> getOptions(@NonNull Class<?> commandClass, int optionPosition, @NonNull Map<DiscordLocale, DiscordLocaleHelper> locales) {
		try {
			@SuppressWarnings("unchecked") //The class must implement an interface, and thus we can assume the correct return type here
			final List<TranslatableOptionData> options = (List<TranslatableOptionData>) commandClass.getMethod("getOptions", int.class).invoke(commandClassHelper.getConstructor(commandClass), optionPosition);
			return options.stream().map(option -> {
				final String name = option.getName();
				final String description = option.getDescription();
				final OptionData optionData = option
						.toOptionData(DEFAULT_LOCALE.t(name), DEFAULT_LOCALE.t(name));
				locales.forEach((language, locale) -> optionData
						.setNameLocalization(language, locale.t(name).toLowerCase())
						.setDescriptionLocalization(language, locale.t(description)));
				return optionData;
			}).toList();
		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
			log.error("Failed to getOptions {}", e.getMessage());
			return Collections.emptyList();
		}
	}
}
