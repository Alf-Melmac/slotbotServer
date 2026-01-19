package de.webalf.slotbot.model.dtos.website.event.creation;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.webalf.slotbot.model.dtos.minimal.MinimalEventFieldDto;
import de.webalf.slotbot.model.dtos.website.event.EventActionDto;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Alf
 * @since 25.07.2022
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = {@JsonCreator})
@Value
@SuperBuilder
public class EventPostDto extends EventActionDto {
	@Size(max = 22)
	List<MinimalEventFieldDto> details;

	List<MinimalSquadDto> squadList;
}
