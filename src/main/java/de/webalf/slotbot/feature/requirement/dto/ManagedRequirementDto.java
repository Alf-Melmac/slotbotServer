package de.webalf.slotbot.feature.requirement.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 09.02.2025
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class ManagedRequirementDto extends RequirementDto {
	boolean fulfilled;
}
