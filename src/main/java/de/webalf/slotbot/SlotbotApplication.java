package de.webalf.slotbot;

import de.webalf.slotbot.service.bot.BotService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;

/**
 * @author Alf
 * @since 22.06.2020
 */
@SpringBootApplication
@EnableCaching
@ConfigurationPropertiesScan("de.webalf.slotbot.configuration.properties")
public class SlotbotApplication {

	public static void main(String[] args) {
		//Start spring application
		final ApplicationContext applicationContext = SpringApplication.run(SlotbotApplication.class, args);

		//Start discord bot
		applicationContext.getBean(BotService.class).startUp();
	}
}