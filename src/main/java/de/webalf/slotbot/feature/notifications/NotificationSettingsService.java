package de.webalf.slotbot.feature.notifications;

import de.webalf.slotbot.feature.notifications.dto.NotificationSettingPutDto;
import de.webalf.slotbot.feature.notifications.model.NotificationSetting;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.util.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Alf
 * @since 11.08.2021
 */
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationSettingsService {
	private final NotificationSettingRepository notificationSettingRepository;

	/**
	 * Finds all {@link NotificationSetting}s for the given {@link User} that don't have an event link
	 *
	 * @param user to get settings for
	 * @return all global settings of user
	 */
	public List<NotificationSetting> findSettings(User user) {
		return notificationSettingRepository.findAllByUserAndEventIsNull(user);
	}

	/**
	 * Finds all {@link NotificationSetting}s for the given {@link User} for the given {@link Event}
	 *
	 * @param user  to be notified
	 * @param event to notify about
	 * @return all event specific settings or global settings
	 */
	public List<NotificationSetting> findSettings(User user, Event event) {
		final List<NotificationSetting> settingsForEvent = notificationSettingRepository.findAllByUserAndEvent(user, event);
		return settingsForEvent.isEmpty() ? findSettings(user) : settingsForEvent;
	}

	/**
	 * Sets the public {@link NotificationSetting}s for the given {@link User}
	 *
	 * @param user to update settings for
	 * @param dtos new complete list with new or updated settings
	 * @return saved public notification settings
	 */
	List<NotificationSetting> updateGlobalNotificationSettings(User user, @NonNull List<NotificationSettingPutDto> dtos) {
		List<NotificationSetting> notificationSettings = new ArrayList<>();
		dtos.forEach(notificationSettingDto ->
				notificationSettings.add(updateOrCreateNotificationSetting(user, notificationSettingDto)));

		//Delete removed settings
		final List<NotificationSetting> existingSettings = new ArrayList<>(findSettings(user));
		existingSettings.removeIf(existingSetting ->
				notificationSettings.stream().anyMatch(notificationSetting -> existingSetting.getId() == notificationSetting.getId()));
		notificationSettingRepository.deleteAll(existingSettings);

		return notificationSettings;
	}

	/**
	 * Updates or creates a new {@link NotificationSetting} for the given user
	 *
	 * @param user to notify
	 * @param dto  new or updated setting
	 * @return saved setting
	 */
	private NotificationSetting updateOrCreateNotificationSetting(User user, @NonNull NotificationSettingPutDto dto) {
		final Supplier<NotificationSetting> newSetting = () -> NotificationSetting.builder().user(user).build();
		final NotificationSetting notificationSetting = dto.id() == null
				? newSetting.get()
				: notificationSettingRepository.findById(dto.id())
				.orElseGet(newSetting);

		DtoUtils.ifPresent(dto.hoursBeforeEvent(), notificationSetting::setHoursBeforeEvent);
		DtoUtils.ifPresent(dto.minutesBeforeEvent(), notificationSetting::setMinutesBeforeEvent);

		return notificationSettingRepository.save(notificationSetting);
	}
}
