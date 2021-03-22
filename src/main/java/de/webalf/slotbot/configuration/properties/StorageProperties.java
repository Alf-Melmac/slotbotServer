package de.webalf.slotbot.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

/**
 * @author Alf
 * @since 21.03.2021
 */
@ConfigurationProperties("storage")
@Data
public class StorageProperties {
	@NotBlank
	private String download = "./download";

	@NotBlank
	private String images = "./img";
}
