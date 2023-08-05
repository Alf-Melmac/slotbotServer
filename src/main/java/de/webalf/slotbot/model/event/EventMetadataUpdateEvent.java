package de.webalf.slotbot.model.event;

/**
 * Event for metadata changes to an event:
 * <ul>
 *     <li>Event name</li>
 *     <li>Hidden status</li>
 *     <li>Date time</li>
 * </ul>
 *
 * @author Alf
 * @since 05.08.2023
 */
public record EventMetadataUpdateEvent(long eventId) {}
