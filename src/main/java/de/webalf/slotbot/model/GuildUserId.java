package de.webalf.slotbot.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * @author Alf
 * @since 25.09.2023
 */
@Embeddable
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GuildUserId implements Serializable {
	private long guildId;
	private long userId;
}
