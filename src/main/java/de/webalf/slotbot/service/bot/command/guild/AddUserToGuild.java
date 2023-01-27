package de.webalf.slotbot.service.bot.command.guild;

import de.webalf.slotbot.model.annotations.bot.ContextMenu;
import de.webalf.slotbot.service.bot.command.DiscordUserContext;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import static de.webalf.slotbot.util.bot.InteractionUtils.reply;

/**
 * @author Alf
 * @since 27.01.2023
 */
@Slf4j
@ContextMenu(name = "bot.context.guild.addUserToGuild",
		type = Command.Type.USER,
		authorization = Permission.MANAGE_ROLES)
public class AddUserToGuild implements DiscordUserContext {
	@Override
	public void perform(@NonNull UserContextInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("User context: addUserToGuild");

		reply(event, locale.t("bot.context.guild.addUserToGuild"));
	}
}
