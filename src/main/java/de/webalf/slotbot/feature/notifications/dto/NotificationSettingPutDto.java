package de.webalf.slotbot.feature.notifications.dto;

import de.webalf.slotbot.feature.notifications.model.NotificationSetting;
import lombok.Builder;

/**
 * Update DTO for {@link NotificationSetting}
 *
 * @author Alf
 * @since 06.12.2025
 */
@Builder
public record NotificationSettingPutDto(Long id, int hoursBeforeEvent, int minutesBeforeEvent) {}
