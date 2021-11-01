package de.webalf.slotbot.model.annotations;

import de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization;
import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.*;

import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.ADMINISTRATIVE;

/**
 * @author Alf
 * @since 15.07.2021
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(SlashCommands.class)
@IndexAnnotated
public @interface SlashCommand {
	String name();

	String description();

	Authorization authorization() default ADMINISTRATIVE;

	int optionPosition() default -1;
}
