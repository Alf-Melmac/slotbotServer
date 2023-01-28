package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.GuildUsers;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.repository.GuildUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alf
 * @since 18.01.2023
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuildUsersService {
	private final GuildUsersRepository guildUsersRepository;
	private final UserService userService;
	private final GuildService guildService;

	public List<User> getUsers(Guild guild) {
		return guildUsersRepository.findByGuild(guild).stream().map(GuildUsers::getUser).toList();
	}

	public GuildUsers add(long guildId, long userId) {
		final Guild guild = guildService.find(guildId);
		final User user = userService.find(userId);

		return guildUsersRepository.findByGuildAndUser(guild, user)
				.orElseGet(() -> guildUsersRepository.save(GuildUsers.builder().user(user).guild(guild).build()));
	}

	public void remove(long guildId, long userId) {
		final User user = userService.findExisting(userId);
		final Guild guild = guildService.findExisting(guildId);

		guildUsersRepository.deleteByGuildAndUser(guild, user);
	}
}
