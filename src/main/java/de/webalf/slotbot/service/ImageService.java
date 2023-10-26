package de.webalf.slotbot.service;

import de.webalf.slotbot.configuration.properties.StorageProperties;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.util.permissions.PermissionHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Alf
 * @since 11.10.2023
 */
@Service
@RequiredArgsConstructor
public class ImageService {
	private final StorageProperties storageProperties;

	private final Tika tika = new Tika();

	/**
	 * Saves the given file to the userContent folder of the logged-in user
	 *
	 * @param file to save
	 * @return filename of the saved file
	 * @throws IOException if file input stream can't be read
	 */
	public String writeImage(@NonNull MultipartFile file) throws IOException {
		final String originalFilename = file.getOriginalFilename();
		final String mediaType = tika.detect(file.getInputStream(), originalFilename);
		if (!(mediaType.equals(MediaType.IMAGE_JPEG_VALUE) || mediaType.equals(MediaType.IMAGE_PNG_VALUE)) || file.getSize() == 0) {
			throw BusinessRuntimeException.builder().title("File is not an image").build();
		}
		if (originalFilename == null) {
			throw BusinessRuntimeException.builder().title("File has no name").build();
		}
		final String extension = FilenameUtils.getSuffixFromPath(originalFilename);
		final String filename = originalFilename
				.substring(0, originalFilename.length() - extension.length())
				.replaceAll("[^a-zA-Z0-9()\\[\\]\\s._-]", "");
		if (filename.isBlank()) {
			throw BusinessRuntimeException.builder().title("File name is invalid").build();
		}
		final Path directory = Paths.get(storageProperties.getUserContent(), PermissionHelper.getLoggedInUserId());
		//noinspection ResultOfMethodCallIgnored
		directory.toFile().mkdirs();
		Path finalPath = directory.resolve(filename + extension);
		for (int i = 2; Files.exists(finalPath); i++) {
			finalPath = directory.resolve(filename + " (" + i + ")" + extension);
		}
		file.transferTo(finalPath);
		return finalPath.getFileName().toString();
	}
}
