package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.model.annotations.SlashCommand;
import de.webalf.slotbot.util.permissions.ApplicationPermissionHelper;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.atteo.classindex.ClassIndex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 18.07.2021
 */
@UtilityClass
public final class SlashCommandUtils {
	public static final Map<String, Class<?>> commandToClassMap = new HashMap<>();

	static {
		final Iterable<Class<?>> commandList = ClassIndex.getAnnotated(SlashCommand.class);
		StreamSupport.stream(commandList.spliterator(), false)
				.forEach(command -> Arrays.stream(CommandClassHelper.getSlashCommand(command))
						.forEach(slashCommand -> commandToClassMap.put(slashCommand.name().toLowerCase(), command)));
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
	 * Returns the {@link CommandPrivilege}s that include allowed roles for the given {@link SlashCommand}
	 *
	 * @param guild        to get roles from
	 * @param slashCommand to get allowed roles for
	 * @return command privileges with allowed roles
	 * @see #getAllowedRoles(SlashCommand)
	 */
	public static Set<CommandPrivilege> getCommandPrivileges(@NonNull Guild guild, SlashCommand slashCommand) {
		return getAllowedRoles(slashCommand).stream()
				.map(discordRole -> CommandPrivilege.enableRole(guild.getRolesByName(discordRole, false).get(0).getId()))
				.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Returns all discord roles that are allowed to use the given {@link SlashCommand}
	 *
	 * @param command to get allowed roles for
	 * @return names of allowed discord roles
	 */
	private static Set<String> getAllowedRoles(@NonNull SlashCommand command) {
		return Arrays.stream(command.authorization().getRoles()).map(ApplicationPermissionHelper.Role::getDiscordRole).collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Returns the string value of the given not null {@link OptionMapping}
	 *
	 * @param option to get text from
	 * @return string
	 */
	public static String getStringOption(@NonNull OptionMapping option) {
		return option.getAsString();
	}

	/**
	 * Returns the string value of the given nullable {@link OptionMapping}
	 *
	 * @param option to get text from
	 * @return string or null
	 */
	public static String getOptionalStringOption(OptionMapping option) {
		return option == null ? null : getStringOption(option);
	}

	/**
	 * Returns the integer value of the given not null {@link OptionMapping}
	 *
	 * @param option to get int from
	 * @return int
	 */
	public static int getIntegerOption(@NonNull OptionMapping option) {
		return Math.toIntExact(option.getAsLong());
	}

	/**
	 * Returns the id of the {@link User} of the given not null {@link OptionMapping}
	 *
	 * @param option to get user id from
	 * @return user id
	 */
	public static long getUserOption(@NonNull OptionMapping option) {
		return option.getAsUser().getIdLong();
	}

	/**
	 * Returns the user id of the given nullable {@link OptionMapping}
	 *
	 * @param option to get user from
	 * @return user id or null
	 */
	public static Long getOptionalUserOption(OptionMapping option) {
		return option == null ? null : getUserOption(option);
	}
}
