package de.webalf.slotbot.model.annotations.bot;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.*;

/**
 * @author Alf
 * @since 22.08.2023
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ButtonInteractions.class)
@IndexAnnotated
public @interface ButtonInteraction {
	/**
	 * Button id prefix
	 */
	String value();
}
