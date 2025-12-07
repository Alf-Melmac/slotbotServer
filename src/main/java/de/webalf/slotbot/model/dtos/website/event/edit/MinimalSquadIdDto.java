package de.webalf.slotbot.model.dtos.website.event.edit;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @author Alf
 * @since 20.08.2022
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = {@JsonCreator})
@Value
@SuperBuilder
public class MinimalSquadIdDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	List<MinimalSlotIdDto> slotList;

	String reservedFor;

	Set<Long> requirements;
}
