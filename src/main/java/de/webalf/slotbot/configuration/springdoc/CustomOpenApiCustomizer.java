package de.webalf.slotbot.configuration.springdoc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.servers.Server;
import lombok.NonNull;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

import static de.webalf.slotbot.constant.Urls.API;

/**
 * Extends the {@link OpenApiConfig}.
 *
 * @author Alf
 * @since 11.06.2023
 */
@Configuration
public class CustomOpenApiCustomizer implements OpenApiCustomizer {
	@Override
	public void customise(@NonNull OpenAPI openApi) {
		//Remove api prefix from all endpoints...
		final Paths newPaths = new Paths();
		openApi.getPaths().keySet().forEach(path -> newPaths.addPathItem(path.replaceFirst("^" + API, ""), openApi.getPaths().get(path)));
		openApi.setPaths(newPaths);

		//...and add it to the servers
		final List<Server> servers = openApi.getServers();
		servers.forEach(server -> server.setUrl(server.getUrl() + API));

		//Order tags
		TagNames.orderTags(openApi);

		//Remove minLength 0 from all models
		openApi.getComponents().getSchemas().values().forEach(value -> value.getProperties().values().forEach(property -> {
			if (property instanceof final StringSchema schemaProperty &&
					Objects.equals(schemaProperty.getMinLength(), 0)) {
				schemaProperty.setMinLength(null);
			}
		}));
	}
}
