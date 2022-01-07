package de.webalf.slotbot.util;

import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.service.GuildService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;
import java.time.ZonedDateTime;

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

	public MessageEmbed buildDetailsEmbed(@NonNull EventApiDto event) {
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.decode(event.getEventType().getColor()))
				.setTitle(event.getName(), fixUrl(event.getUrl(), event.getOwnerGuild()))
				.setDescription(event.getDescription())
				.setThumbnail(event.getPictureUrl())
				.setFooter(event.getEventType().getName() + " Mission von " + event.getCreator())
				.setTimestamp(Instant.now());

		if (Boolean.TRUE.equals(event.getHidden())) {
			embedBuilder.setImage("https://cdn.discordapp.com/attachments/759147249325572097/789151354920632330/hidden_event.jpg");
		}

		addFields(embedBuilder, event);

		return embedBuilder.build();
	}

	private void addFields(@NonNull EmbedBuilder embedBuilder, @NonNull EventApiDto event) {
		addField("Zeitplan", buildScheduleField(event.getDateTimeZoned(), event.getMissionLength()), embedBuilder);
		addField("Missionstyp", event.getMissionType(), true, embedBuilder);
		addField("Reserve nimmt teil", buildReserveParticipatingField(event.getReserveParticipating()), true, embedBuilder);
		event.getDetails().forEach(field -> {
			String text = field.getText();
			if ("true".equals(text)) {
				text = "Ja";
			} else if ("false".equals(text)) {
				text = "Nein";
			}
			if (StringUtils.isNotEmpty(field.getLink())) {
				text = "[" + text + "](" + fixUrl(field.getLink(), event.getOwnerGuild()) + ")";
			}
			addField(field.getTitle(), text, true, embedBuilder);
		});
	}

	private String fixUrl(String url, String ownerGuild) {
		return EventUtils.fixUrl(url, guildService.findByDiscordGuild(Long.parseLong(ownerGuild)));
	}

	private static String buildScheduleField(ZonedDateTime eventDateTime, String missionLength) {
		final String dateTimeText = DATE_TIME_SHORT.format(eventDateTime) + " Uhr";
		return StringUtils.isNotEmpty(missionLength) ? dateTimeText + " und dauert " + missionLength : dateTimeText;
	}

	private static String buildReserveParticipatingField(Boolean reserveParticipating) {
		if (reserveParticipating == null) {
			return null;
		}
		return reserveParticipating ? "Ja" : "Nein";
	}
}
