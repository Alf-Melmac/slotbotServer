package de.webalf.slotbot.service.external;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * @author Alf
 * @since 24.01.2021
 */
@Service
@Slf4j
public class ExternalServerService {
	private static final ProcessBuilder PROCESS_BUILDER = new ProcessBuilder();

	static {
		PROCESS_BUILDER.redirectErrorStream(true); //Redirects stderr to stdout
	}

	public void toggleServer(@NonNull BattlemetricsApiService.Identifier identifier, boolean start) {
		if (identifier.getRelationships().isArma() && identifier.isServerEmpty()) {
			if (start) {
				startArmaServer();
			} else {
				stopArmaServer();
			}
			execute();
		} else {
			throw BusinessRuntimeException.builder().title("Server must be an Arma 3 server and empty.").build();
		}
	}

	private static void startArmaServer() {
		log.debug("Starting arma server...");
		PROCESS_BUILDER.command("sh", "-c", "ssh armaserver '\".\\Desktop\\Server Autostart.lnk\"'");
	}

	private static void stopArmaServer() {
		log.debug("Stopping arma server...");
		PROCESS_BUILDER.command("sh", "-c", "ssh armaserver '\".\\Desktop\\Server Autostop.lnk\"'");
	}

	private static void execute() {
		Process process;
		try {
			process = PROCESS_BUILDER.start();
		} catch (IOException e) {
			log.error("Error starting shell command for starting arma server", e);
			return;
		}

		final InputStream inputStream = process.getInputStream();

		if (log.isDebugEnabled() || log.isTraceEnabled()) {
			Executors.newSingleThreadExecutor().submit(() ->
					new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
							.lines()
							.forEach(line -> {
								log.trace(line);
								if (line.contains("Autostart beendet!")) {
									log.debug("Finished arma server startup");
								} else if (line.contains("Server beendet!")) {
									log.debug("Finished arma stopping");
								}
							}));
		}

		try {
			log.debug("Server start exitCode: " + process.waitFor());
		} catch (InterruptedException e) {
			log.error("Server start wait for Interrupted", e);
			Thread.currentThread().interrupt();
		}
	}
}
