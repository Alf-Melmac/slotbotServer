package de.webalf.slotbot.feature.discord_webhook_events;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.pando.crypto.nacl.Crypto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * @author Alf
 * @see <a href="https://discord.com/developers/docs/events/webhook-events">Webhook Events</a>
 * @since 04.11.2024
 */
@RestController
@RequestMapping("/external/discord")
@RequiredArgsConstructor
@Slf4j
public class DiscordWebhookEventsController {
	private final DiscordProperties discordProperties;
	private final DiscordWebhookEventsHandler discordWebhookEventsHandler;

	@PostMapping
	public ResponseEntity<Void> handleEvent(@NonNull HttpServletRequest request) throws IOException {
		// Need to read the raw body for the signature validation. That's why we can't use @RequestBody
		final String body = request.getReader().lines().collect(Collectors.joining());

		if (!validateRequest(request, body)) {
			log.debug("Invalid signature: {}", request.getRemoteAddr());
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		discordWebhookEventsHandler.handle(body);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/**
	 * Validate the Ed25519 signature
	 *
	 * @see <a href="https://discord.com/developers/docs/events/webhook-events#setting-up-an-endpoint-validating-security-request-headers">Validating Security Request Headers</a>
	 */
	private boolean validateRequest(@NonNull HttpServletRequest request, String body) {
		final String signature = request.getHeader("X-Signature-Ed25519");
		final String timestamp = request.getHeader("X-Signature-Timestamp");

		try {
			return Crypto.signVerify(
					Crypto.signingPublicKey(Hex.decodeHex(discordProperties.getPublicKey())),
					(timestamp + body).getBytes(StandardCharsets.UTF_8),
					Hex.decodeHex(signature));
		} catch (DecoderException e) {
			log.error("Error while decoding signature", e);
			return false;
		}
	}
}
