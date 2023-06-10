package de.webalf.slotbot.model.dtos.minimal;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 20.08.2022
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@SuperBuilder
//Just for the readability
@JsonPropertyOrder({"id", "title", "text"})
public class MinimalEventFieldIdDto extends MinimalEventFieldDto implements IdEntity {
	private long id;
}
