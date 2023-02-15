package de.webalf.slotbot.util;

import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.service.GuildService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Locale;

import static de.webalf.slotbot.util.bot.EmbedUtils.addField;
import static net.dv8tion.jda.api.utils.TimeFormat.DATE_TIME_SHORT;

/**
 * @author Alf
 * @since 05.01.2022
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventHelper {
	private final GuildService guildService;
	private final MessageSource messageSource;

	public MessageEmbed buildDetailsEmbed(@NonNull EventApiDto event, @NonNull Locale guildLocale) {
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.decode(event.getEventType().getColor()))
				.setTitle(event.getName(), buildUrl(event.getId(), event.getOwnerGuild()))
				.setDescription(event.getDescription())
				.setThumbnail(event.getPictureUrl())
				.setFooter(messageSource.getMessage("bot.embed.event.footer", new String[]{event.getEventType().getName(), event.getCreator()}, guildLocale))
				.setTimestamp(Instant.now());

		if (Boolean.TRUE.equals(event.getHidden())) {
			embedBuilder.setImage("https://cdn.discordapp.com/attachments/759147249325572097/789151354920632330/hidden_event.jpg");
		}

		addFields(embedBuilder, event, guildLocale);

		return embedBuilder.build();
	}

	private void addFields(@NonNull EmbedBuilder embedBuilder, @NonNull EventApiDto event, @NonNull Locale guildLocale) {
		addField(messageSource.getMessage("bot.embed.event.details.schedule", null, guildLocale),
				buildScheduleField(event.getDateTimeZoned(), event.getMissionLength(), guildLocale),
				embedBuilder);
		addField(messageSource.getMessage("bot.embed.event.details.missionType", null, guildLocale),
				event.getMissionType(),
				true,
				embedBuilder);
		addField(messageSource.getMessage("bot.embed.event.details.reserveParticipating", null, guildLocale),
				buildReserveParticipatingField(event.getReserveParticipating(), guildLocale),
				true,
				embedBuilder);
		event.getDetails().forEach(field -> {
			String text = field.getText();
			if ("true".equals(text)) {
				text = messageSource.getMessage("yes", null, guildLocale);
			} else if ("false".equals(text)) {
				text = messageSource.getMessage("no", null, guildLocale);
			}
			if (StringUtils.isNotEmpty(field.getLink())) {
				text = "[" + text + "](" + field.getLink() + ")";
			}
			addField(field.getTitle(), text, true, embedBuilder);
		});
	}

	private String buildUrl(long eventId, String ownerGuild) {
		return EventUtils.buildUrl(eventId, guildService.findByDiscordGuild(Long.parseLong(ownerGuild)));
	}

	private String buildScheduleField(ZonedDateTime eventDateTime, String missionLength, @NonNull Locale guildLocale) {
		final String dateTimeText = DATE_TIME_SHORT.format(eventDateTime) + (guildLocale.getLanguage().equals("de") ? " Uhr" : "");
		return StringUtils.isNotEmpty(missionLength) ?
				messageSource.getMessage("bot.embed.event.details.schedule.text", new String[]{dateTimeText, missionLength}, guildLocale) : dateTimeText;
	}

	private String buildReserveParticipatingField(Boolean reserveParticipating, @NonNull Locale guildLocale) {
		if (reserveParticipating == null) {
			return null;
		}
		return reserveParticipating ? messageSource.getMessage("yes", null, guildLocale) : messageSource.getMessage("no", null, guildLocale);
	}
}
