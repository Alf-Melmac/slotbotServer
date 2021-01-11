package de.webalf.slotbot.processor;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.service.UpdateInterceptorService;
import lombok.RequiredArgsConstructor;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author Alf
 * @since 29.12.2020
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class HibernateInterceptor extends EmptyInterceptor {
	private static final long serialVersionUID = 7037471511333181486L;

	private final UpdateInterceptorService updateInterceptorService;

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		updateInterceptorService.update(entity, currentState, previousState, propertyNames);
		return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
	}

	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		//The reserve is removed only in conjunction with another "slot-creating" action. Therefore, no update needs to be made in this case
		if (!(entity instanceof Squad && ((Squad) entity).isReserve() || entity instanceof Slot && ((Slot) entity).isInReserve())) {
			updateInterceptorService.update(entity);
		}
		super.onDelete(entity, id, state, propertyNames, types);
	}
}
