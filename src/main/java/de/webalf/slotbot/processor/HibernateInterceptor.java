package de.webalf.slotbot.processor;

import de.webalf.slotbot.service.UpdateInterceptorService;
import lombok.RequiredArgsConstructor;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

/**
 * @author Alf
 * @since 29.12.2020
 */
@Component
@RequiredArgsConstructor
public class HibernateInterceptor implements Interceptor {
	private final UpdateInterceptorService updateInterceptorService;

	@Override
	public boolean onFlushDirty(Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
		updateInterceptorService.update(entity, currentState, previousState, propertyNames);
		return Interceptor.super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
	}

	@Override
	public void onDelete(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
		updateInterceptorService.onDelete(entity);
		Interceptor.super.onDelete(entity, id, state, propertyNames, types);
	}

	@Override
	public void onCollectionUpdate(Object collection, Object key) throws CallbackException {
		updateInterceptorService.onCollectionUpdate(collection);
		Interceptor.super.onCollectionUpdate(collection, key);
	}
}
