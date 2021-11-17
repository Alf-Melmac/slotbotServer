package de.webalf.slotbot.model;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Alf
 * @since 31.08.2021
 */
public class NotificationMap<K, V extends ScheduledFuture<?>> extends HashMap<K, V> {
	private static final long serialVersionUID = -7586981330194453423L;

	@Override
	public V remove(Object key) {
		final V value = get(key);
		if (value != null) {
			value.cancel(true);
		}
		return super.remove(key);
	}

	@Override
	public void clear() {
		values().forEach(value -> value.cancel(true));
		super.clear();
	}
}
