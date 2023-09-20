package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.GuildUsers;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.repository.GuildUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Alf
 * @since 18.01.2023
 */
@Service
@Transactional
@RequiredArgsConstructor
public class GuildUsersService {
	private final GuildUsersRepository guildUsersRepository;
	private final UserService userService;
	private final GuildService guildService;

	public Page<User> getUsers(Guild guild, Pageable pageable) {
		return guildUsersRepository.findByGuild(guild, pageable).map(GuildUsers::getUser);
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

	@Async
	public void removeOptional(long guildId, long userId) {
		final Optional<User> user = userService.findExistingOptional(userId);
		if (user.isEmpty()) {
			return;
		}
		final Optional<Guild> guild = guildService.findExistingOptional(guildId);
		if (guild.isEmpty()) {
			return;
		}

		guildUsersRepository.deleteByGuildAndUser(guild.get(), user.get());
	}
}
