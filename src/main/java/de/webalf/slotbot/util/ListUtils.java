package de.webalf.slotbot.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to work with {@link List}s
 *
 * @author Alf
 * @since 14.11.2020
 */
@UtilityClass
public final class ListUtils {
	/**
	 * Removes the given element from the collection
	 *
	 * @param list   to filter
	 * @param filter element to remove
	 * @return filtered list
	 */
	public static <T> List<T> getListFiltered(@NonNull Collection<T> list, T filter) {
		return list.stream().filter(element -> !element.equals(filter)).collect(Collectors.toList());
	}

	/**
	 * Removes the first element from the list and returns that removed element.
	 *
	 * @param list to shift
	 * @return first now removed element
	 */
	public static <T> T shift(@NonNull List<T> list) {
		if (list.isEmpty()) {
			return null;
		}
		final T firstEl = list.get(0);
		list.remove(0);
		return firstEl;
	}

	/**
	 * Checks if the list is empty
	 *
	 * @param list to check
	 * @return true if the list is empty
	 */
	public static boolean zeroArguments(@NonNull Collection<?> list) {
		return CollectionUtils.isEmpty(list);
	}

	/**
	 * Checks if the list has only one element inside
	 *
	 * @param list to check
	 * @return true if only one argument is inside the list
	 */
	public static boolean oneArgument(@NonNull Collection<?> list) {
		return list.size() == 1;
	}

	/**
	 * Checks if the list has two element inside
	 *
	 * @param list to check
	 * @return true if two arguments are inside the list
	 */
	public static boolean twoArguments(@NonNull Collection<?> list) {
		return list.size() == 2;
	}

	/**
	 * Boxes the elements of the primitive type array
	 *
	 * @param intArray array with elements to be boxed
	 * @return array with boxed elements
	 */
	public static Integer[] convertToBoxedArray(int[] intArray) {
		return Arrays.stream(intArray).boxed().toArray(Integer[]::new);
	}
}
