package de.webalf.slotbot.model.annotations.bot;

import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import net.dv8tion.jda.api.Permission;
import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.*;

/**
 * @author Alf
 * @since 15.07.2021
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(SlashCommands.class)
@IndexAnnotated
public @interface SlashCommand {
	/**
	 * Translatable name key
	 */
	String name();

	/**
	 * Translatable description key
	 */
	String description();

	Permission authorization();

	/**
	 * Determines if options are available and which index should be used for {@link DiscordSlashCommand#getOptions(int)}
	 */
	int optionPosition() default -1;
}
