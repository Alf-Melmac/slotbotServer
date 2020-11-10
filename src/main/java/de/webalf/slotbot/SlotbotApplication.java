package de.webalf.slotbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author Alf
 * @since 22.06.2020
 */
@SpringBootApplication
@EnableCaching
public class SlotbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SlotbotApplication.class, args);
	}

}
