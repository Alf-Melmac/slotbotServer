package de.webalf.slotbot.feature.notifications.dto;

import de.webalf.slotbot.feature.notifications.model.NotificationSetting;
import lombok.Builder;

/**
 * DTO for {@link NotificationSetting}
 *
 * @author Alf
 * @since 12.08.2021
 */
@Builder
public record NotificationSettingDto(long id, int hoursBeforeEvent, int minutesBeforeEvent) {}
