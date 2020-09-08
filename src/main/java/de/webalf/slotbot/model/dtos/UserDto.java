package de.webalf.slotbot.model.dtos;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Alf
 * @since 07.09.2020
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserDto {
	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number.
	String id;

	@Builder
	public UserDto(String id) {
		this.id = id;
	}
}
