package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.NotificationSetting;
import de.webalf.slotbot.model.dtos.referenceless.NotificationSettingsReferencelessDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 12.08.2021
 */
@UtilityClass
public final class NotificationSettingAssembler {
	private NotificationSettingsReferencelessDto toReferencelessDto(NotificationSetting notificationSetting) {
		return NotificationSettingsReferencelessDto.builder()
				.hoursBeforeEvent(notificationSetting.getHoursBeforeEvent())
				.minutesBeforeEvent(notificationSetting.getMinutesBeforeEvent())
				.build();
	}

	public static List<NotificationSettingsReferencelessDto> toReferencelessDtoList(Iterable<? extends NotificationSetting> notificationSettings) {
		return StreamSupport.stream(notificationSettings.spliterator(), false)
				.map(NotificationSettingAssembler::toReferencelessDto)
				.collect(Collectors.toList());
	}
}
