package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.OldEvent;
import de.webalf.slotbot.model.dtos.EventTypeDto;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.repository.OldEventRepository;
import de.webalf.slotbot.util.DtoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alf
 * @since 14.05.2021
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventMigrationService {
	private final OldEventRepository oldEventRepository;
	private final EventRepository eventRepository;
	private final EventTypeService eventTypeService;

	public void migrate() {
		List<OldEvent> oldEvents = oldEventRepository.findAll();
		final EventType eventType = eventTypeService.find(EventTypeDto.builder().name("Arma 3").color("#0000FF").build());

		oldEvents.forEach(oldEvent -> {
			log.info("Migrating {} ({})", oldEvent.getName(), oldEvent.getId());
			final Event newEvent = Event.builder()
					.id(oldEvent.getId())
					.eventType(eventType)
					.name(oldEvent.getName())
					.dateTime(oldEvent.getDateTime())
					.creator(oldEvent.getCreator())
					.hidden(oldEvent.isHidden())
					.channel(oldEvent.getChannel())
					.squadList(oldEvent.getSquadList())
					.infoMsg(oldEvent.getInfoMsg())
					.slotListMsg(oldEvent.getSlotListMsg())
					.description(oldEvent.getDescription())
					.pictureUrl(oldEvent.getPictureUrl())
					.missionType(oldEvent.getMissionType())
					.missionLength(oldEvent.getMissionLength())
					.reserveParticipating(oldEvent.getReserveParticipating())
					.build();
			newEvent.setDetails(buildDetails(oldEvent, newEvent));
			eventRepository.save(newEvent);
		});
	}

	private static final String MAP = "Karte";
	private static final String MODPACK = "Modpack";
	private static final String RESPAWN = "Respawn";
	private static final String MISSION_TIME = "Missionszeit";
	private static final String NAVIGATION = "Navigation";
	private static final String MEDICAL = "Medic-System";
	private static final String TELEPORT = "Technischer Teleport";

	private List<EventField> buildDetails(OldEvent oldEvent, Event newEvent) {
		List<EventField> eventFields = new ArrayList<>();

		//Karte
		DtoUtils.ifPresent(oldEvent.getMap(),
				map -> eventFields.add(EventField.builder().title(MAP).text(map).event(newEvent).build()));

		//Modpack
		DtoUtils.ifPresent(oldEvent.getModPack(),
				modpack -> eventFields.add(EventField.builder().title(MODPACK).text(modpack).event(newEvent).build()));

		//Respawn
		DtoUtils.ifPresent(oldEvent.getRespawn(),
				respawn -> eventFields.add(EventField.builder().title(RESPAWN).text(Boolean.toString(respawn)).event(newEvent).build()));

		//Missionszeit
		DtoUtils.ifPresent(oldEvent.getMissionTime(),
				missionTime -> eventFields.add(EventField.builder().title(MISSION_TIME).text(missionTime).event(newEvent).build()));

		//Navigation
		DtoUtils.ifPresent(oldEvent.getNavigation(),
				navigation -> eventFields.add(EventField.builder().title(NAVIGATION).text(navigation).event(newEvent).build()));

		//Medic-System
		DtoUtils.ifPresent(oldEvent.getMedicalSystem(),
				medical -> eventFields.add(EventField.builder().title(MEDICAL).text(medical).event(newEvent).build()));

		//Technischer Teleport
		DtoUtils.ifPresent(oldEvent.getTechnicalTeleport(),
				teleport -> eventFields.add(EventField.builder().title(TELEPORT).text(teleport).event(newEvent).build()));

		return eventFields;
	}
}
