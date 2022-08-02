package de.webalf.slotbot.model.dtos.website;

import lombok.Builder;
import lombok.Value;

/**
 * @author Alf
 * @since 01.08.2022
 */
@Value
@Builder
public class AuthenticatedUserDto {
	String id;
	String name;
	String avatarUrl;
}
