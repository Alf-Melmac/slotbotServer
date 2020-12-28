package de.webalf.slotbot.assembler.website;

import de.webalf.slotbot.controller.website.EventWebController;
import de.webalf.slotbot.model.ActionLog;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.website.ActionLogDto;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 20.11.2020
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ActionLogAssembler implements RepresentationModelAssembler<ActionLog, ActionLogDto> {
	private final EventService eventService;
	private final UserService userService;

	@Override
	public ActionLogDto toModel(ActionLog log) {
		ActionLogDto logDto = ActionLogDto.builder()
				.user(userService.getUserNameDto(log.getUser()))
				.action(log.getAction())
				.timeLeft(log.getTimeGapString())
				.build();
		setObjectAttributes(logDto, log.getActionObjectId());
		return logDto;
	}

	private void setObjectAttributes(ActionLogDto logDto, long objectId) {
		if (logDto.isEventAction()) {
			setEventAttributes(logDto, objectId);
		}
	}

	private void setEventAttributes(ActionLogDto logDto, long eventId) {
		Event event = eventService.findById(eventId);
		logDto.setObjectName(event.getName());
		logDto.setObjectUrl(linkTo(methodOn(EventWebController.class).getEventDetailsHtml(event.getId())).toUri().toString());
	}
}
