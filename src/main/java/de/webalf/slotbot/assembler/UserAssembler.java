package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.util.LongUtils;
import org.springframework.stereotype.Component;

/**
 * @author Alf
 * @since 07.09.2020
 */
@Component
public class UserAssembler {
	public static User fromDto(UserDto userDto) {
		if (userDto == null) {
			return null;
		}

		return User.builder()
				.id(LongUtils.parseLong(userDto.getId()))
				.build();
	}

	public static UserDto toDto(User user) {
		if (user == null) {
			return null;
		}

		return UserDto.builder()
				.id(LongUtils.toString(user.getId()))
				.build();
	}
}
