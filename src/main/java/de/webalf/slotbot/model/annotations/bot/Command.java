package de.webalf.slotbot.model.annotations.bot;

import de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization;
import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Alf
 * @since 02.01.2021
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@IndexAnnotated
@Deprecated
public @interface Command {
	String[] names();

	String description() default "";

	String usage() default "";

	int[] argCount() default {0};

	Authorization authorization() default Authorization.SYS_ADMINISTRATION;

	boolean dmAllowed() default false;
}
