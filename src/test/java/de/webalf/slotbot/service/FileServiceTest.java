package de.webalf.slotbot.service;

import de.webalf.slotbot.configuration.properties.StorageProperties;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the path containment of {@link FileService FileService#getResource}. All public methods delegate to it, so
 * {@link FileService#loadAsResource(String)} is used for single segment and
 * {@link FileService#loadUserContentAsResource(String, String)} for multi segment paths.
 *
 * @author Alf
 * @since 21.11.2026
 */
class FileServiceTest {
	@TempDir
	private Path storage;

	private FileService sut;

	private static final String FILE_NAME = "file.txt";
	private static final String FILE_CONTENT = "download-content";
	private static final String IMAGE_NAME = "image.png";
	private static final String IMAGE_CONTENT = "image-content";

	@BeforeEach
	void setUp() throws IOException {
		final Path download = Files.createDirectory(storage.resolve("download"));
		final Path userContent = Files.createDirectory(storage.resolve("userContent"));
		final Path userDirectory = Files.createDirectory(userContent.resolve("userid"));
		final Path siblingDirectory = Files.createDirectory(storage.resolve("download-secret"));

		Files.writeString(download.resolve(FILE_NAME), FILE_CONTENT);
		Files.createDirectory(download.resolve("directory"));
		Files.writeString(userDirectory.resolve(IMAGE_NAME), IMAGE_CONTENT);
		Files.writeString(storage.resolve("secret.txt"), "secret");
		Files.writeString(siblingDirectory.resolve("secret.txt"), "secret");

		final StorageProperties storageProperties = new StorageProperties();
		storageProperties.setDownload(download.toString());
		storageProperties.setUserContent(userContent.toString());
		sut = new FileService(storageProperties);
	}

	@Test
	void getResourceReturnsFileInBaseDirectory() throws IOException {
		assertThat(sut.loadAsResource(FILE_NAME).getContentAsByteArray()).asString().isEqualTo(FILE_CONTENT);
	}

	@Test
	void getResourceReturnsFileInNestedDirectory() throws IOException {
		assertThat(sut.loadUserContentAsResource("userid", IMAGE_NAME).getContentAsByteArray()).asString().isEqualTo(IMAGE_CONTENT);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"missing.txt",
			"directory",
			"..",
			"../secret.txt",
			"../download-secret/secret.txt",
			"file\u0000.txt"
	})
	void getResourceRejectsInvalidOrOutsideFiles(String filename) {
		assertThrows(ResourceNotFoundException.class, () -> sut.loadAsResource(filename));
	}

	@Test
	void getResourceRejectsAbsolutePathOutsideBaseDirectory() {
		final String absolutePath = storage.resolve("secret.txt").toString();
		assertThrows(ResourceNotFoundException.class, () -> sut.loadAsResource(absolutePath));
	}

	@Test
	void getResourceRejectsLeadingSegmentOutsideBaseDirectory() {
		assertThrows(ResourceNotFoundException.class, () -> sut.loadUserContentAsResource("..", "secret.txt"));
	}

	@Test
	void getResourceRejectsAbsoluteLeadingSegmentOutsideBaseDirectory() {
		final String absolutePath = storage.toString();
		assertThrows(ResourceNotFoundException.class, () -> sut.loadUserContentAsResource(absolutePath, "secret.txt"));
	}
}
