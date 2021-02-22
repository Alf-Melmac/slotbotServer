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
	@SuppressWarnings("FieldMayBeFinal") //Must be able to be set in order to use ReflectionUtils.shallowCopyFieldState
	private String id;

//	private final String steam64Id; //TODO add steamId
}
