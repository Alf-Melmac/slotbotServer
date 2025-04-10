package de.webalf.slotbot.feature.action_log;

import de.webalf.slotbot.assembler.website.DiscordUserAssembler;
import de.webalf.slotbot.feature.action_log.dto.ActionLogDto;
import de.webalf.slotbot.feature.action_log.model.ActionLog;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.external.discord.DiscordGuildMember;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.external.DiscordBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 07.03.2025
 */
@Component
@RequiredArgsConstructor
class ActionLogAssembler {
	private final DiscordBotService discordBotService;
	private final EventService eventService;

	private ActionLogDto toDto(ActionLog actionLog) {
		final Event event = eventService.findById(actionLog.getActionObjectId());
		final DiscordGuildMember member = discordBotService.getGuildMember(actionLog.getUser().getId(), event.getOwnerGuild().getId());

		return ActionLogDto.builder()
				.id(actionLog.getId())
				.action(actionLog.getAction())
				.timeGap(actionLog.getTimeGap())
				.user(DiscordUserAssembler.toDto(member))
				.build();
	}

	List<ActionLogDto> toDtoList(Iterable<? extends ActionLog> actionLogs) {
		return StreamSupport.stream(actionLogs.spliterator(), false)
				.map(this::toDto)
				.toList();
	}
}
