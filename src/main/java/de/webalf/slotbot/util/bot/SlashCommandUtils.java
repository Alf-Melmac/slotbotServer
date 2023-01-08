package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.atteo.classindex.ClassIndex;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.util.bot.DiscordLocaleHelper.DEFAULT_LOCALE;

/**
 * @author Alf
 * @since 18.07.2021
 */
@UtilityClass
public final class SlashCommandUtils {
	private static final Map<String, Class<?>> commandToClassMap = new HashMap<>();

	static {
		final Iterable<Class<?>> commandList = ClassIndex.getAnnotated(SlashCommand.class);
		StreamSupport.stream(commandList.spliterator(), false)
				.forEach(command -> Arrays.stream(CommandClassHelper.getSlashCommand(command))
						.forEach(slashCommand -> commandToClassMap.put(DEFAULT_LOCALE.t(slashCommand.name()).toLowerCase(), command)));
	}

	/**
	 * Searches for the given slash command the matching class annotated with {@link SlashCommand}
	 *
	 * @param command to search
	 * @return matching class or null if not found
	 */
	public static Class<?> get(@NonNull String command) {
		return commandToClassMap.get(command.toLowerCase());
	}

	/**
	 * Returns all classes annotated with {@link SlashCommand}
	 *
	 * @return all slash command classes
	 */
	public static Collection<Class<?>> get() {
		return commandToClassMap.values();
	}

	private static OptionMapping getOptionMapping(@NonNull CommandInteractionPayload interaction, @NonNull String option) {
		return interaction.getOption(DEFAULT_LOCALE.t(option));
	}

	/**
	 * Asserts that the {@link #getOptionalStringOption(CommandInteractionPayload, String) optional string option}  is present
	 */
	public static String getStringOption(@NonNull CommandInteractionPayload interaction, @NonNull String option) {
		return getOptionalStringOption(interaction, option);
	}

	/**
	 * Returns the string value of the given nullable option
	 *
	 * @return string or null
	 */
	public static String getOptionalStringOption(@NonNull CommandInteractionPayload interaction, @NonNull String option) {
		final OptionMapping optionMapping = getOptionMapping(interaction, option);
		return optionMapping == null ? null : optionMapping.getAsString();
	}

	/**
	 * Asserts that the {@link #getOptionalIntegerOption(CommandInteractionPayload, String) optional int option} is present
	 */
	public static int getIntegerOption(@NonNull CommandInteractionPayload interaction, @NonNull String option) {
		//noinspection DataFlowIssue Caller only calls with required options
		return getOptionalIntegerOption(interaction, option);
	}

	/**
	 * Returns the integer value of the given nullable option
	 *
	 * @return int or null
	 */
	public static Integer getOptionalIntegerOption(@NonNull CommandInteractionPayload interaction, @NonNull String option) {
		final OptionMapping optionMapping = getOptionMapping(interaction, option);
		return optionMapping == null ? null : optionMapping.getAsInt();
	}

	/**
	 * Asserts that the {@link #getOptionalUserOption(CommandInteractionPayload, String) optional user option} is present and returns it id
	 */
	public static long getUserOption(@NonNull CommandInteractionPayload interaction, @NonNull String option) {
		//noinspection DataFlowIssue Caller only calls with required options
		return getOptionalUserOption(interaction, option);
	}

	/**
	 * Returns the user id of the given nullable option
	 *
	 * @return user id or null
	 */
	public static Long getOptionalUserOption(@NonNull CommandInteractionPayload interaction, @NonNull String option) {
		final OptionMapping optionMapping = getOptionMapping(interaction, option);
		return optionMapping == null ? null : optionMapping.getAsUser().getIdLong();
	}
}
