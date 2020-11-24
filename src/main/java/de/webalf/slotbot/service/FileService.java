package de.webalf.slotbot.service;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Alf
 * @since 24.11.2020
 */
@Service
@Slf4j
public class FileService {
	private static final Path ROOT = Paths.get("src/main/resources/static/download");

	/**
	 * Returns the given file by name as a {@link Resource}
	 *
	 * @param filename file to load
	 * @return file as {@link Resource}
	 * @throws ResourceNotFoundException if file doesn't exists or the path is a malformedURL
	 */
	public Resource loadDownload(String filename) {
		try {
			Resource resource = new UrlResource(ROOT.resolve(filename).toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new ResourceNotFoundException(filename);
			}
		} catch (MalformedURLException e) {
			log.error("MalformedURLException encountered while loading file " + filename, e);
			throw new ResourceNotFoundException(filename);
		}
	}
}
