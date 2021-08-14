package de.webalf.slotbot.service;

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
	 * @return all public settings of user
	 */
	public List<NotificationSetting> findAllPublicSettings(User user) {
		return notificationSettingRepository.findAllByUserAndEventIsNull(user);
	}

	/**
	 * Sets the public {@link NotificationSetting}s for the given {@link User}
	 *
	 * @param user                    to update settings for
	 * @param notificationSettingDtos new complete list with new or updated settings
	 * @return saved public notification settings
	 */
	public List<NotificationSetting> updatePublicNotificationSettings(User user, @NonNull List<NotificationSettingDto> notificationSettingDtos) {
		List<NotificationSetting> notificationSettings = new ArrayList<>();
		notificationSettingDtos.forEach(notificationSettingDto ->
				notificationSettings.add(updateOrCreateNotificationSetting(user, notificationSettingDto)));

		//Delete removed settings
		final List<NotificationSetting> existingSettings = new ArrayList<>(findAllPublicSettings(user));
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
	public void deleteAllByUser(User user) {
		notificationSettingRepository.deleteAllByUser(user);
	}
}
