package de.webalf.slotbot.configuration.springdoc;

import de.webalf.slotbot.configuration.authentication.api.TokenAuthFilter;
import de.webalf.slotbot.exception.ExceptionResponse;
import de.webalf.slotbot.model.annotations.springdoc.Resource;
import de.webalf.slotbot.model.authentication.ApiTokenType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;

import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * {@link OpenAPI} configuration. Further refined by {@link CustomOpenApiCustomizer}.
 *
 * @author Alf
 * @since 09.10.2021
 */
@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {
	@Value("#{servletContext.contextPath}")
	private String servletContextPath;

	private final TokenAuthFilter tokenAuthFilter;

	private static final String API_AUTH_TOKEN_START = "API Auth Token (";
	private static final String END = ")";
	public static final String SECURITY_KEY_READ_PUBLIC = API_AUTH_TOKEN_START + ApiTokenType.TypeRoleNames.READ_PUBLIC + END;
	public static final String SECURITY_KEY_READ = API_AUTH_TOKEN_START + ApiTokenType.TypeRoleNames.READ + END;
	public static final String SECURITY_KEY_WRITE = API_AUTH_TOKEN_START + ApiTokenType.TypeRoleNames.WRITE + END;
	public static final String SECURITY_KEY_ADMIN = API_AUTH_TOKEN_START + ApiTokenType.TypeRoleNames.ADMIN + END;

	@Bean
	public OpenAPI api() {
		final String authTokenName = tokenAuthFilter.getTokenName();
		return new OpenAPI()
				.info(new Info()
						.title("Slotbot API")
						.description("API für den Slotbot")
						.version("v1")
						.contact(new io.swagger.v3.oas.models.info.Contact()
								.name("Alf")
								.email("slotbot-api@webalf.de"))
						.license(new License()
								.name("GNU Affero General Public License v3.0")
								.url("https://github.com/Alf-Melmac/slotbotServer/blob/master/LICENSE"))
				)
				.components(new Components()
						.addSecuritySchemes(SECURITY_KEY_READ_PUBLIC, new SecurityScheme().name(authTokenName).type(SecurityScheme.Type.APIKEY).in(HEADER))
						.addSecuritySchemes(SECURITY_KEY_READ, new SecurityScheme().name(authTokenName).type(SecurityScheme.Type.APIKEY).in(HEADER))
						.addSecuritySchemes(SECURITY_KEY_WRITE, new SecurityScheme().name(authTokenName).type(SecurityScheme.Type.APIKEY).in(HEADER))
						.addSecuritySchemes(SECURITY_KEY_ADMIN, new SecurityScheme().name(authTokenName).type(SecurityScheme.Type.APIKEY).in(HEADER)))
				;
	}

	@Bean
	public OperationCustomizer customizeOperation() {
		return (operation, handlerMethod) -> {
			if (handlerMethod.hasMethodAnnotation(SecurityRequirement.class) || handlerMethod.getMethod().getDeclaringClass().getAnnotation(SecurityRequirement.class) != null) {
				operation.responses(operation.getResponses()
						.addApiResponse("403", new ApiResponse().description("Forbidden")));
			}
			if (handlerMethod.hasMethodAnnotation(Resource.class)) {
				final Schema<ExceptionResponse> exceptionResponseSchema = new Schema<>();
				exceptionResponseSchema.set$ref(ExceptionResponse.class.getSimpleName());
				//noinspection DataFlowIssue hasMethodAnnotation ensured the presence of the Resource annotation
				operation.responses(operation.getResponses()
						.addApiResponse("404", new ApiResponse()
								.description("Not Found")
								.content(new Content()
										.addMediaType(APPLICATION_JSON_VALUE, new MediaType()
												.schema(exceptionResponseSchema)
												.example(String.format("""
																{
																    "errorMessage": "Resource not found",
																    "requestedURI": "%s"
																}""",
														servletContextPath +
																handlerMethod.getMethod().getDeclaringClass().getAnnotation(RequestMapping.class).value()[0] +
																handlerMethod.getMethodAnnotation(Resource.class).value())
														.replaceAll("(?<!^)\\{[^}]*}", "123"))
										))));
			}
			return operation;
		};
	}
}
