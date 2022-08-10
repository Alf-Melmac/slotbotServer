package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.NotificationSetting;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.NotificationSettingDto;
import de.webalf.slotbot.repository.NotificationSettingRepository;
import de.webalf.slotbot.util.DtoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alf
 * @since 11.08.2021
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
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
	public List<NotificationSetting> updateGlobalNotificationSettings(User user, @NonNull List<NotificationSettingDto> dtos) {
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
	private NotificationSetting updateOrCreateNotificationSetting(User user, @NonNull NotificationSettingDto dto) {
		final NotificationSetting notificationSetting = notificationSettingRepository.findById(dto.getId()).orElseGet(() -> NotificationSetting.builder().user(user).build());

		DtoUtils.ifPresent(dto.getHoursBeforeEvent(), notificationSetting::setHoursBeforeEvent);
		DtoUtils.ifPresent(dto.getMinutesBeforeEvent(), notificationSetting::setMinutesBeforeEvent);

		return notificationSettingRepository.save(notificationSetting);
	}

	/**
	 * Removes all {@link NotificationSetting}s for the given user
	 *
	 * @param user to deactivate notification for
	 */
	@Deprecated
	public void deleteAllByUser(User user) {
		notificationSettingRepository.deleteAllByUser(user);
	}
}
