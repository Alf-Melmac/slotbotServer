package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alf
 * @since 14.11.2020
 */
@UtilityClass
public final class ListUtils {
	/**
	 * Removes the given element from the String collection
	 * 
	 * @param list to filter
	 * @param filter element to remove
	 * @return filtered list
	 */
	public static List<String> getListFiltered(Collection<String> list, String filter) {
		return list.stream().filter(element -> !element.equals(filter)).collect(Collectors.toList());
	}
}
