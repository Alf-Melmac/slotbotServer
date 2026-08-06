package de.webalf.slotbot.model.dtos.website.guild;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @param groupIdentifier name of the new community
 * @author Alf
 * @since 09.07.26
 */
public record GuildCreateDto(@NotBlank @Size(max = TEXT) String groupIdentifier) {}
