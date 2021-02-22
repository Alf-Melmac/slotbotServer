package de.webalf.slotbot.model.dtos;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 07.09.2020
 */
@Data
@SuperBuilder
public class UserDto {
	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number.
	private String id;

	private String steamId64;
}
