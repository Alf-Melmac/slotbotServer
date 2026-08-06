package de.webalf.slotbot.util;

import de.webalf.slotbot.model.HasId;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 19.07.26
 */
@UtilityClass
public class EntityUtils {
	/**
	 * Returns the id of the given entity as string if it is not null, otherwise null
	 *
	 * @param entity to get id from
	 * @param <T>    type of entity
	 * @return id of entity or null
	 */
	public static <T extends HasId> String getIdStringIfPresent(T entity) {
		if (entity == null) {
			return null;
		}
		return Long.toString(entity.getId());
	}
}
