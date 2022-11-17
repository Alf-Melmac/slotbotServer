package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.annotations.SlashCommand;
import de.webalf.slotbot.util.bot.CommandClassHelper;
import de.webalf.slotbot.util.bot.SlashCommandUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.bot.CommandClassHelper.getSlashCommand;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getCommandPrivileges;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.NONE;

/**
 * @author Alf
 * @since 15.07.2021
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlashCommandsService {
	private final CommandClassHelper commandClassHelper;

	/**
	 * Updates slash commands in the given guild
	 *
	 * @param guild to update commands for
	 */
	public void updateCommands(@NonNull Guild guild) {
		log.info("Updating commands for {}...", guild.getName());
		final List<CommandData> commands = SlashCommandUtils.commandToClassMap.values().stream()
				.distinct()
				.flatMap(slashCommandClass -> { //For each slash command
					final SlashCommand[] slashCommands = getSlashCommand(slashCommandClass);
					Set<CommandData> commandDataList = new HashSet<>();
					for (SlashCommand slashCommand : slashCommands) {
						final CommandData commandData = new CommandData(slashCommand.name().toLowerCase(), slashCommand.description()); //Create Command data
						if (slashCommand.optionPosition() >= 0) { //Add options if present
							commandData.addOptions(getOptions(slashCommandClass, slashCommand.optionPosition()));
						}
						if (slashCommand.authorization() != NONE) {
							commandData.setDefaultEnabled(false);
						}
						commandDataList.add(commandData);
					}
					return commandDataList.stream();
				}).collect(Collectors.toUnmodifiableList());

		log.info("Found {} commands. Starting update...", commands.size());
		guild.updateCommands().addCommands(commands).queue(updatedCommands -> updatedCommands.forEach(command -> { //Update discord commands
			final SlashCommand slashCommand = CommandClassHelper.getSlashCommand(command.getName());
			if (slashCommand.authorization() != NONE) { //Set authorized roles if needed
				guild.updateCommandPrivilegesById(command.getIdLong(), getCommandPrivileges(guild, slashCommand)).queue();
			}
		}));
		log.info("Finished command update for {}.", guild.getName());
	}

	@SuppressWarnings("unchecked") //The class must implement an interface, and thus we can assume the correct return type here
	private List<OptionData> getOptions(Class<?> commandClass, int optionPosition) {
		try {
			return (List<OptionData>) commandClass.getMethod("getOptions", int.class).invoke(commandClassHelper.getConstructor(commandClass), optionPosition);
		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
			log.error("Failed to getOptions {}", e.getMessage());
			return Collections.emptyList();
		}
	}
}
