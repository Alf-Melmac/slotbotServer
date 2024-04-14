package de.webalf.slotbot.model.dtos.website.profile;

import lombok.Builder;

/**
 * @author Alf
 * @since 14.04.2024
 */
@Builder
public record LastEventInfo(long id, long daysSince) {}
