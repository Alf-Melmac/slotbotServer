package de.webalf.slotbot.exception;

import lombok.NonNull;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * @author Alf
 * @since 12.06.2021
 */
public class IgnoreErrorResponseErrorHandler extends DefaultResponseErrorHandler {
	@Override
	public void handleError(@NonNull ClientHttpResponse response) {
		//Do nothing
	}
}
