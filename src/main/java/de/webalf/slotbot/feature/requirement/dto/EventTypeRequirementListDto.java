package de.webalf.slotbot.feature.requirement.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 01.12.2024
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventTypeRequirementListDto extends RequirementListDto {
	boolean active;
}
