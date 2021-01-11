package de.webalf.slotbot.model.annotations;

import de.webalf.slotbot.util.PermissionHelper.Authorization;
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
public @interface Command {
	String name();

	String description() default "";

	String usage() default "";

	int[] argCount() default {0};

	Authorization authorization() default Authorization.ADMINISTRATIVE;

	boolean dmAllowed() default false;
}
