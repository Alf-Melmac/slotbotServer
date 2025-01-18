package de.webalf.slotbot;

import de.webalf.slotbot.feature.notifications.EventNotificationService;
import de.webalf.slotbot.service.FileService;
import de.webalf.slotbot.service.bot.BotService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * @author Alf
 * @since 22.06.2020
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableMethodSecurity
@ConfigurationPropertiesScan("de.webalf.slotbot.configuration.properties")
@EnableJpaRepositories(bootstrapMode = BootstrapMode.DEFERRED)
/*This annotation (with bootstrapMode) had to be introduced with spring-boot-starter-parent version 2.3.7.RELEASE. Without it, the application cannot start.
"Error creating bean with name 'entityManagerFactory': Requested bean is currently in creation: Is there an unresolvable circular reference?"
The breaking change was made here: https://github.com/spring-projects/spring-boot/issues/24249
This fix is described in https://github.com/spring-projects/spring-framework/issues/25111#issuecomment-696062762. If this issue gets resolved the annotation should be removed again to follow the default.*/
public class SlotbotApplication {
	public static void main(String[] args) {
		//Start spring application
		final ApplicationContext applicationContext = SpringApplication.run(SlotbotApplication.class, args);

		//Initial fetch of file directories
		applicationContext.getBean(FileService.class).listFiles();

		//Start discord bot
		applicationContext.getBean(BotService.class).startUp();

		//Create all notifications
		applicationContext.getBean(EventNotificationService.class).rebuildAllNotifications();
	}
}