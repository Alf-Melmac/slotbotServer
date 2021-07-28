package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.model.annotations.SlashCommand;
import de.webalf.slotbot.util.permissions.ApplicationPermissionHelper;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
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
				.forEach(command -> commandToClassMap.put(CommandClassHelper.getSlashCommand(command).name().toLowerCase(), command));
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
}
