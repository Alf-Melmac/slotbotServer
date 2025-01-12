package de.webalf.slotbot.feature.notifications;

import de.webalf.slotbot.feature.notifications.dto.NotificationSettingDto;
import de.webalf.slotbot.feature.notifications.model.NotificationSetting;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 12.08.2021
 */
@UtilityClass
public final class NotificationSettingAssembler {
	private static NotificationSettingDto toReferencelessDto(NotificationSetting notificationSetting) {
		return NotificationSettingDto.builder()
				.id(notificationSetting.getId())
				.hoursBeforeEvent(notificationSetting.getHoursBeforeEvent())
				.minutesBeforeEvent(notificationSetting.getMinutesBeforeEvent())
				.build();
	}

	public static List<NotificationSettingDto> toReferencelessDtoList(Iterable<? extends NotificationSetting> notificationSettings) {
		return StreamSupport.stream(notificationSettings.spliterator(), false)
				.map(NotificationSettingAssembler::toReferencelessDto)
				.toList();
	}
}
