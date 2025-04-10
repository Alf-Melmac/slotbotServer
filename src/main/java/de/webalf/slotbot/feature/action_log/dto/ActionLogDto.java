package de.webalf.slotbot.feature.action_log.dto;

import de.webalf.slotbot.feature.action_log.model.LogAction;
import de.webalf.slotbot.model.dtos.website.DiscordUserDto;
import lombok.Builder;

import java.time.Duration;

/**
 * @author Alf
 * @since 07.03.2025
 */
@Builder
public record ActionLogDto(long id, LogAction action, Duration timeGap, DiscordUserDto user) {}
