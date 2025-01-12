package de.webalf.slotbot.feature.notifications;

import de.webalf.slotbot.feature.notifications.model.NotificationSetting;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.repository.SuperIdEntityJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alf
 * @since 11.08.2021
 */
@Repository
interface NotificationSettingRepository extends SuperIdEntityJpaRepository<NotificationSetting> {
	List<NotificationSetting> findAllByUserAndEventIsNull(User user);

	List<NotificationSetting> findAllByUserAndEvent(User user, Event event);
}
