package de.webalf.slotbot.service;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.URL;

/**
 * @author Alf
 * @since 24.11.2020
 */
@Service
@Slf4j
public class FileService {
	/**
	 * Returns the given file by name as a {@link Resource}
	 *
	 * @param filename file to load
	 * @return file as {@link Resource}
	 * @throws ResourceNotFoundException if file doesn't exists or the path is a malformedURL
	 */
	public Resource loadDownload(String filename) {
		URL url = FileService.class.getClassLoader().getResource("static/download/" + filename);
		if (url != null) {
			Resource resource = new UrlResource(url);
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
		}

		throw new ResourceNotFoundException(filename);
	}
}
