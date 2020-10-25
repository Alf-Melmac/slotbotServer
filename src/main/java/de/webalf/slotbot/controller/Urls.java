package de.webalf.slotbot.controller;

import de.webalf.slotbot.controller.website.StartWebController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 26.09.2020
 */
public final class Urls {
	private static final String SLOTBOT = "/slotbot";

	public static final String API = SLOTBOT + "/api";

	public static final String START_URL = linkTo(methodOn(StartWebController.class).getStart()).toUri().toString();
}
