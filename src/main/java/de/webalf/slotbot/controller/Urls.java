package de.webalf.slotbot.controller;

import lombok.Value;

/**
 * @author Alf
 * @since 26.09.2020
 */
@Value
public class Urls {
	private static final String SLOTBOT = "/slotbot";

	public static final String API = SLOTBOT + "/api/v1";

	public static final String ADMIN = "/admin";
}
