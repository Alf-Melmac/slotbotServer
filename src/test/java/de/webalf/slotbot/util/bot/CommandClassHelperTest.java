package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.model.annotations.bot.ContextMenu;
import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.model.annotations.bot.StringSelectInteraction;
import org.atteo.classindex.ClassIndex;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * @author Alf
 * @since 10.04.2023
 */
@ExtendWith(MockitoExtension.class)
class CommandClassHelperTest {
	@InjectMocks
	CommandClassHelper sut;

	@ParameterizedTest(name = "{0}")
	@MethodSource("provideDiscordSlashCommandClasses")
	void getConstructorForAllDiscordSlashCommandClasses(Class<?> command) {
		assertDoesNotThrow(() -> sut.getConstructor(command));
	}

	private static Iterable<Class<?>> provideDiscordSlashCommandClasses() {
		return ClassIndex.getAnnotated(SlashCommand.class);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("provideDiscordStringSelectClasses")
	void getConstructorForAllDiscordStringSelectClasses(Class<?> command) {
		assertDoesNotThrow(() -> sut.getConstructor(command));
	}

	private static Iterable<Class<?>> provideDiscordStringSelectClasses() {
		return ClassIndex.getAnnotated(StringSelectInteraction.class);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("provideDiscordUserContextClasses")
	void getConstructorForAllDiscordUserContextClasses(Class<?> command) {
		assertDoesNotThrow(() -> sut.getConstructor(command));
	}

	private static Iterable<Class<?>> provideDiscordUserContextClasses() {
		return ClassIndex.getAnnotated(ContextMenu.class);
	}
}
