package de.webalf.slotbot.model.dtos;

import de.webalf.slotbot.model.dtos.referenceless.NotificationSettingsReferencelessDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 12.08.2021
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@SuperBuilder
public class NotificationSettingDto extends NotificationSettingsReferencelessDto {
	private EventDto event;
}
