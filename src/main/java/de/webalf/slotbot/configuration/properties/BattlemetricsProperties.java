package de.webalf.slotbot.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Alf
 * @since 19.01.2021
 */
@ConfigurationProperties("battlemetrics")
@Data
public class BattlemetricsProperties {
	@NotBlank
	private String apiAccessToken;

	private List<Integer> serverIds;
}
