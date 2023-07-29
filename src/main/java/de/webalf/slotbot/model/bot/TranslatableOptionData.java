package de.webalf.slotbot.model.bot;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * Copy of {@link OptionData} to support translation keys in name and description
 *
 * @author Alf
 * @since 07.01.2023
 */
public record TranslatableOptionData(OptionType type, String name, String description, boolean required) {
	public OptionData toOptionData(String name, String description) {
		return new OptionData(type, name, description, required);
	}
}
