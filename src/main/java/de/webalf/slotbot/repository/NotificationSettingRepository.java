package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.NotificationSetting;
import de.webalf.slotbot.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alf
 * @since 11.08.2021
 */
@Repository
public interface NotificationSettingRepository extends SuperIdEntityJpaRepository<NotificationSetting> {
	List<NotificationSetting> findAllByUserAndEventIsNull(User user);

	List<NotificationSetting> findAllByUserAndEvent(User user, Event event);

	@Deprecated
	void deleteAllByUser(User user);
}
