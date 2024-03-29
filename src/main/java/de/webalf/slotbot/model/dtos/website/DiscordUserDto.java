package de.webalf.slotbot.model.dtos.website;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

/**
 * @author Alf
 * @since 01.08.2022
 */
@Value
@Builder
public class DiscordUserDto {
	String id;
	String name;
	String avatarUrl;
	Set<String> authorities;
}
