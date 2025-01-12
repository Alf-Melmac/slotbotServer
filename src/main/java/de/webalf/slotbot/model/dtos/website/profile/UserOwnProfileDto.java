package de.webalf.slotbot.model.dtos.website.profile;

import de.webalf.slotbot.feature.notifications.dto.NotificationSettingDto;
import jakarta.validation.constraints.NotBlank;
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
	List<NotificationSettingDto> notificationSettings;
	boolean externalCalendarIntegrationActive;
	@NotBlank
	String icsCalendarUrl;
}
