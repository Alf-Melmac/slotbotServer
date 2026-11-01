package de.webalf.slotbot.service;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.repository.GuildRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static de.webalf.slotbot.AssertionUtils.assertMessageEquals;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Alf
 * @since 01.11.2026
 */
@ExtendWith(MockitoExtension.class)
class GuildServiceTest {
	@Mock
	private GuildRepository guildRepository;

	@InjectMocks
	private GuildService sut;

	//create
	@Test
	void createSavesGuild() {
		final String name = "Guild";
		nameNotTaken(name);
		when(guildRepository.saveAndFlush(any(Guild.class))).thenAnswer(returnsFirstArg());

		final Guild guild = sut.create(name);

		assertEquals(name, guild.getGroupIdentifier());
	}

	@Test
	void createThrowsOnDuplicateName() {
		final String name = "Guild";
		nameTaken(name);

		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () -> sut.create(name));
		assertMessageEquals("Guild with name " + name + " already exists", exception);
		verify(guildRepository, never()).saveAndFlush(any());
	}

	//findOrCreateByDiscordId
	@Test
	void findOrCreateByDiscordIdCreatesNewGuild() {
		final long discordId = 123;
		final String name = "Foo";
		discordIdNotTaken(discordId);
		nameNotTaken(name);
		when(guildRepository.saveAndFlush(any(Guild.class))).thenAnswer(returnsFirstArg());

		final Guild guild = sut.findOrCreateByDiscordId(discordId, name);

		assertThat(guild)
				.extracting(Guild::getDiscordId, Guild::getGroupIdentifier)
				.containsExactly(discordId, name);
	}

	@Test
	void findOrCreateByDiscordIdRenamesExistingGuild() {
		final long discordId = 123;
		final String newName = "New";
		final Guild existing = buildGuild("Old");
		discordIdTaken(discordId, existing);
		nameNotTaken(newName);
		when(guildRepository.saveAndFlush(any(Guild.class))).thenAnswer(returnsFirstArg());

		final Guild guild = sut.findOrCreateByDiscordId(discordId, newName);

		assertSame(existing, guild);
		assertEquals(newName, guild.getGroupIdentifier());
	}

	@Test
	void findOrCreateByDiscordIdKeepsOwnName() {
		final int discordId = 123;
		final String guildName = "Foo";
		final Guild existing = buildGuild(guildName);
		discordIdTaken(discordId, existing);
		nameTaken(guildName, existing);
		when(guildRepository.saveAndFlush(any(Guild.class))).thenAnswer(returnsFirstArg());

		final Guild guild = sut.findOrCreateByDiscordId(discordId, guildName);
		assertEquals(existing, guild);
		assertEquals(guildName, guild.getGroupIdentifier());
	}

	@Test
	void findOrCreateByDiscordIdKeepsOwnNameWhenLoadedAsOtherInstance() {
		final int discordId = 123;
		final String guildName = "Foo";
		final Guild existing = Guild.builder().id(7).groupIdentifier(guildName).build();
		discordIdTaken(discordId, existing);
		nameTaken(guildName, Guild.builder().id(7).groupIdentifier(guildName).build());
		when(guildRepository.saveAndFlush(any(Guild.class))).thenAnswer(returnsFirstArg());

		assertEquals(guildName, sut.findOrCreateByDiscordId(discordId, guildName).getGroupIdentifier());
	}

	@Test
	void findOrCreateByDiscordIdAppendsSuffixOnDuplicateName() {
		final int discordId = 123;
		discordIdNotTaken(discordId);
		final String takenName = "Foo";
		nameTaken(takenName);
		final String expectedName = "Foo (2)";
		nameNotTaken(expectedName);
		when(guildRepository.saveAndFlush(any(Guild.class))).thenAnswer(returnsFirstArg());

		final Guild guild = sut.findOrCreateByDiscordId(discordId, takenName);
		assertEquals(expectedName, guild.getGroupIdentifier());
	}

	@Test
	void findOrCreateByDiscordIdIncrementsSuffixUntilUnused() {
		final int discordId = 123;
		discordIdNotTaken(discordId);
		final String takenName = "Foo";
		nameTaken(takenName);
		nameTaken("Foo (2)");
		final String expectedName = "Foo (3)";
		nameNotTaken(expectedName);
		when(guildRepository.saveAndFlush(any(Guild.class))).thenAnswer(returnsFirstArg());

		assertEquals(expectedName, sut.findOrCreateByDiscordId(discordId, takenName).getGroupIdentifier());
	}

	@Test
	void findOrCreateByDiscordIdTruncatesLongName() {
		final String longName = "a".repeat(TEXT + 20);
		final String truncatedName = "a".repeat(TEXT);
		final int discordId = 123;
		discordIdNotTaken(discordId);
		nameNotTaken(truncatedName);
		when(guildRepository.saveAndFlush(any(Guild.class))).thenAnswer(returnsFirstArg());

		final Guild guild = sut.findOrCreateByDiscordId(123, longName);
		assertEquals(truncatedName, guild.getGroupIdentifier());
	}

	@Test
	void findOrCreateByDiscordIdTruncatesLongNameToFitSuffix() {
		final int discordId = 123;
		discordIdNotTaken(discordId);

		final String longName = "a".repeat(TEXT + 20);

		final String truncatedName = "a".repeat(TEXT);
		nameTaken(truncatedName);

		final String suffixedName = "a".repeat(TEXT - 4) + " (2)";
		nameNotTaken(suffixedName);

		when(guildRepository.saveAndFlush(any(Guild.class))).thenAnswer(returnsFirstArg());

		final Guild guild = sut.findOrCreateByDiscordId(123, longName);
		assertEquals(suffixedName, guild.getGroupIdentifier());
	}

	private static Guild buildGuild(String groupIdentifier) {
		return Guild.builder().id(123).groupIdentifier(groupIdentifier).build();
	}

	private static Guild buildOtherGuild(String groupIdentifier) {
		return Guild.builder().id(456).groupIdentifier(groupIdentifier).build();
	}

	private void nameNotTaken(String name) {
		when(guildRepository.findByGroupIdentifier(name)).thenReturn(Optional.empty());
	}

	private void nameTaken(String name) {
		nameTaken(name, buildOtherGuild(name));
	}

	private void nameTaken(String name, Guild guild) {
		when(guildRepository.findByGroupIdentifier(name)).thenReturn(Optional.of(guild));
	}

	private void discordIdNotTaken(long discordId) {
		when(guildRepository.findByDiscordId(discordId)).thenReturn(Optional.empty());
	}

	private void discordIdTaken(long longDiscordId, Guild guild) {
		when(guildRepository.findByDiscordId(longDiscordId)).thenReturn(Optional.of(guild));
	}
}
