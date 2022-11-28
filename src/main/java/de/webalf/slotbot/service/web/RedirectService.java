package de.webalf.slotbot.service.web;

/**
 * @author Alf
 * @since 28.11.2022
 */
public interface RedirectService {
	/**
	 * Creates an absolute path for redirection to the frontend. To do this, the servlet context path is removed
	 *
	 * @param redirectPath relative path
	 */
	String redirectTo(String redirectPath);
}
