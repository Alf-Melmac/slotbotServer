package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.webalf.slotbot.util.bot.MentionUtils;
import lombok.*;

import javax.persistence.*;

/**
 * @author Alf
 * @since 19.06.2021
 */
@Entity
@Table(name = "event_discord_information", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"event_channel"}), @UniqueConstraint(columnNames = {"event_info_msg"}),
		@UniqueConstraint(columnNames = {"event_slotlist_msg_one"}), @UniqueConstraint(columnNames = {"event_slotlist_msg_two"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EventDiscordInformation {
	@Id
	@Column(name = "event_channel", unique = true, nullable = false, updatable = false)
	private long channel;

	@Column(name = "event_info_msg", unique = true)
	private Long infoMsg;

	@Column(name = "event_slotlist_msg_one", unique = true)
	private Long slotListMsgPartOne;

	@Column(name = "event_slotlist_msg_two", unique = true)
	private Long slotListMsgPartTwo;

	@OneToOne(targetEntity = Event.class, optional = false)
	@JoinColumn(name = "event_id")
	@JsonBackReference
	private Event event;

	public String getChannelAsMention() {
		return MentionUtils.getChannelAsMention(getChannel());
	}

	/**
	 * Checks if the event has already been printed. Printing is asserted if an info message id is assigned
	 *
	 * @return true if event has already been printed
	 */
	public boolean isPrinted() {
		return getInfoMsg() != null;
	}
}