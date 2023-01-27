package de.webalf.slotbot.model.annotations.bot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Alf
 * @since 11.12.2022
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@IndexAnnotated
public @interface ContextMenu {
	/**
	 * Translatable name key
	 */
	String name();

	Type type();

	Permission authorization();
}
