package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.SquadAssembler;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.SquadDto;
import de.webalf.slotbot.repository.SquadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Alf
 * @since 23.06.2020
 */
@RequestMapping("/squads")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SquadController {
	private final SquadRepository squadRepository;

	@GetMapping("")
	public Page<SquadDto> getSquads(Pageable pageable) {
		log.debug("getSquads");
		Page<Squad> squadPage = squadRepository.findAll(pageable);
		return SquadAssembler.toEventDtoPage(squadPage, pageable);
	}

	@GetMapping("/{id}")
	public SquadDto getSquadById(@PathVariable(value = "id") long eventId) {
		log.debug("getSquadById: " + eventId);
		Squad squad = squadRepository.findById(eventId).orElseThrow(ResourceNotFoundException::new);
		return SquadAssembler.toEventDto(squad);
	}

	@PostMapping("")
	public SquadDto postSquad(@Valid @RequestBody SquadDto squad) {
		log.debug("postSquad: " + squad.getName());
		return SquadAssembler.toEventDto(squadRepository.save(SquadAssembler.fromDto(squad)));
	}
}
