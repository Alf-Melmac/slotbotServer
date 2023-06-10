package de.webalf.slotbot.constant;

import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 26.09.2020
 */
@UtilityClass
public class Urls {
	private static final String SLOTBOT = "/slotbot";

	public static final String API = SLOTBOT + "/api/v1";

	public static final String UNSTABLE = API + "/unstable";

	public static final String ADMIN = "/admin";
}
