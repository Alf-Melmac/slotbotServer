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

	public List<User> getUsers(Guild guild) {
		return guildUsersRepository.findByGuild(guild).stream().map(GuildUsers::getUser).toList();
	}
}
