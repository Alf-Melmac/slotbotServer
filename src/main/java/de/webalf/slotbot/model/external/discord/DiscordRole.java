package de.webalf.slotbot.model.external.discord;

import lombok.*;

/**
 * Representation of a discord role
 *
 * @author Alf
 * @see net.dv8tion.jda.api.entities.Role
 * @since 27.01.2023
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscordRole {
	private long id;
	private String name;
	private int position;
}
