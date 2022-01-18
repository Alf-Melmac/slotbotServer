package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.util.LongUtils;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 07.09.2020
 */
@UtilityClass
public final class UserAssembler {
	public static User fromDto(UserDto userDto) {
		if (userDto == null) {
			return null;
		}

		return User.builder()
				.id(LongUtils.parseLong(userDto.getId()))
				.steamId64(LongUtils.parseLongWrapper(userDto.getSteamId64()))
				.build();
	}

	public static UserDto toDto(User user) {
		if (user == null) {
			return null;
		}

		return UserDto.builder()
				.id(LongUtils.toString(user.getId()))
				.steamId64(LongUtils.toString(user.getSteamId64()))
				.build();
	}
}
