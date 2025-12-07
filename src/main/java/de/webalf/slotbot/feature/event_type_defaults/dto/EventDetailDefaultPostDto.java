package de.webalf.slotbot.feature.event_type_defaults.dto;

import de.webalf.slotbot.feature.event_type_defaults.model.EventDetailDefault;
import de.webalf.slotbot.model.enums.EventDetailType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.EMBEDDABLE_TITLE;

/**
 * DTO for {@link EventDetailDefault}
 *
 * @author Alf
 * @since 02.12.2024
 */
public record EventDetailDefaultPostDto(Long id,
                                        @NotBlank @Size(max = EMBEDDABLE_TITLE) String title,
                                        @NonNull EventDetailType type,
                                        List<String> selection,
                                        String text) {}
