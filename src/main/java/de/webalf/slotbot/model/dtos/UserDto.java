package de.webalf.slotbot.model.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 07.09.2020
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@SuperBuilder
public class UserDto {
	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number.
	private String id;

	private String steamId64;
}
