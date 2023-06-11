package de.webalf.slotbot.configuration.springdoc;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for sprindocs {@link io.swagger.v3.oas.annotations.tags.Tag Tags}
 *
 * @author Alf
 * @since 11.06.2023
 */
@UtilityClass
public final class TagNames {
	public static final String EVENTS = "Events";
	public static final String SLOTS = "Slots";
	public static final String STATUS = "Status";
	public static final String UNSTABLE = "Unstable";

	private static final List<String> ORDERED_TAG_NAMES = Arrays.asList(EVENTS, SLOTS, STATUS, UNSTABLE);

	/**
	 * Establishes a fixed order for the tags
	 */
	public static void orderTags(@NonNull OpenAPI openAPI) {
		openAPI.getTags()
				.sort(Comparator.comparingInt(tag -> ORDERED_TAG_NAMES.indexOf(tag.getName())));

	}
}
