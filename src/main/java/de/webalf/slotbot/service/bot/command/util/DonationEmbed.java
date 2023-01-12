package de.webalf.slotbot.service.bot.command.util;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.annotations.bot.Command;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.util.StringUtils;
import de.webalf.slotbot.util.bot.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.ADMINISTRATIVE;

/**
 * @author Alf
 * @since 06.07.2021
 */
@Slf4j
@Command(names = {"donationEmbed", "donation"},
		argCount = {1, 3},
		authorization = ADMINISTRATIVE)
public class DonationEmbed implements DiscordCommand {
	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: donationEmbed");

		switch (args.get(0)) {
			case "new":
				message.getChannel().sendMessageEmbeds(buildEmbed()
						.addField("Server Booster", "", false)
						.build()).queue();
				break;
			case "addD", "addDonation":
				final String messageId = args.get(1);
				message.getChannel()
						.retrieveMessageById(messageId)
						.queue(messageWithEmbed -> {
							final MessageEmbed embed = messageWithEmbed.getEmbeds().stream().findAny()
									.orElseThrow(ResourceNotFoundException::new);
							String description = embed.getDescription();
							description = StringUtils.isEmpty(description) ? "- " + args.get(2) :
									description + "\n" + "- " + args.get(2);
							final EmbedBuilder embedBuilder = buildEmbed()
									.setDescription(description);
							embed.getFields().stream().findAny().ifPresent(embedBuilder::addField);
							message.getChannel().editMessageEmbedsById(messageId, embedBuilder.build()).queue();
						});
				break;
			case "addB", "addBoost":
				final String embedId = args.get(1);
				message.getChannel()
						.retrieveMessageById(embedId)
						.queue(messageWithEmbed -> {
							final MessageEmbed embed = messageWithEmbed.getEmbeds().stream().findAny().orElseThrow(ResourceNotFoundException::new);
							String value = embed
									.getFields().stream().findAny().orElseThrow(ResourceNotFoundException::new)
									.getValue();
							value = "‎".equals(value) ? "- " + args.get(2) :
									value + "\n" + "- " + args.get(2);
							message.getChannel().editMessageEmbedsById(embedId, buildEmbed()
											.setDescription(embed.getDescription())
											.addField("Server Booster", value, false).build())
									.queue();
						});
				break;
		}

		MessageUtils.deleteMessagesInstant(message);
	}

	private EmbedBuilder buildEmbed() {
		return new EmbedBuilder()
				.setColor(new Color(32, 34, 37))
				.setTitle("Generöse Spender des Monats " + YearMonth.now().format(DateTimeFormatter.ofPattern("MM.uuuu")))
				.setFooter("AMB dankt euch allen ganz herzlich");
	}
}
