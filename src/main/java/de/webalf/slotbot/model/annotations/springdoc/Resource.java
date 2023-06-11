package de.webalf.slotbot.model.annotations.springdoc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an endpoint as a resource endpoint. This adds the 404 response to the documentation.
 *
 * @author Alf
 * @since 11.06.2023
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Resource {
	/**
	 * Method request mapping. Will be appended to the classes request mapping to build the requestedURI in the example
	 */
	String value() default "";
}
