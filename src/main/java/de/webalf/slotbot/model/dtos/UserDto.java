package de.webalf.slotbot.model.dtos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 07.09.2020
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDto {
	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number.
	private String id;
}
