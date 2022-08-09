package de.webalf.slotbot.model.dtos.website.profile;

import de.webalf.slotbot.model.dtos.referenceless.NotificationSettingsReferencelessDto;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * @author Alf
 * @since 09.08.2022
 */
@Value
@Builder
public class UserOwnProfileDto {
	String steamId64;
	List<NotificationSettingsReferencelessDto> notificationSettings;
	boolean externalCalendarIntegrationActive;
}
