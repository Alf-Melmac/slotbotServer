package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.service.EventDiscordInformationService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Alf
 * @since 03.04.2023
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ChannelDeleteListener extends ListenerAdapter {
	private final EventDiscordInformationService eventDiscordInformationService;

	@Override
	public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
		eventDiscordInformationService.removeByChannel(event.getChannel().getIdLong());
	}
}
