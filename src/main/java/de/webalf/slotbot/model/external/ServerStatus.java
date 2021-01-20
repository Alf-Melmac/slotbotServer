package de.webalf.slotbot.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Alf
 * @since 20.01.2021
 */
public enum ServerStatus {
	@JsonProperty("online")
	ONLINE,
	@JsonProperty("offline")
	OFFLINE,
	@JsonProperty("dead")
	DEAD
}
