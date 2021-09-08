package de.webalf.slotbot.util;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.SqmParser.ReadStep.NONE;
import static de.webalf.slotbot.util.StringUtils.removeNonDigitCharacters;

/**
 * @author Alf
 * @see <a href="https://community.bistudio.com/wiki/Mission.sqm">Mission.sqm</a>
 * @since 05.06.2021
 */
@UtilityClass
@Slf4j
public final class SqmParser {

	public static List<Squad> createSlotListFromFile(MultipartFile file) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			final List<Squad> read = read(reader);
			for (Squad squad : read) {
				squad.setSlotList(squad.getSlotList().stream().sorted(Comparator.comparingInt(Slot::getNumber)).collect(Collectors.toUnmodifiableList()));
			}
			return read;
		} catch (IOException e) {
			log.error("Failed to get, read or buffer file {}", file.getName(), e);
			throw BusinessRuntimeException.builder()
					.title("Die Datei " + file.getName() + " konnte nicht gelesen werden.")
					.description(e.getMessage())
					.cause(e)
					.build();
		}
	}

	private static final String DATA_TYPE_GROUP = "dataType=\"Group\";";
	private static final String CURLY_BRACE_OPEN = "{";
	private static final String CURLY_BRACE_CLOSE = "}";
	private static final String DESCRIPTION = "description=\"";
	private static final String QUOTE_MARK = "\"";

	private static List<Squad> read(BufferedReader reader) throws IOException {
		int lineNumber = 0;

		ReadStep step = NONE;
		int braces = 0;
		Squad nextSquad = null;

		List<Squad> slotList = new ArrayList<>();

		while (true) {
			final String line = reader.readLine();
			if (line == null) break; //EOF
			lineNumber++;

			switch (step) {
				case NONE: //Loop until dataType=Group is found
					if (line.contains(DATA_TYPE_GROUP)) {
						log.trace("Group definition start in line {}", lineNumber);
						step = step.next();
					}
					break;
				case GROUP_SEARCH: //Await opening curly brace for group definition class
					if (line.contains(CURLY_BRACE_OPEN)) {
						log.trace("Group found beginning in line {}", lineNumber);
						step = step.next();
					}
					break;
				case GROUP_FOUND:
					braces = calculateBraces(braces, line);
					if (line.contains(DESCRIPTION)) { //First slot description contains squad/group name
						final String descriptionText = getDescriptionText(line);

						String squadName;
						String slotName;
						if (!descriptionText.contains("@")) {
							log.debug("Line {} is missing group definition", lineNumber);
							squadName = "";
							slotName = descriptionText;
						} else {
							final String[] squadNameSplit = descriptionText.split("@", 2);
							squadName = squadNameSplit[1];
							slotName = squadNameSplit[0];
						}

						nextSquad = Squad.builder().name(squadName).slotList(new ArrayList<>()).build();
						log.trace("Created new Squad '{}'", nextSquad.getName());
						readSlot(slotName, nextSquad);
						step = step.next();
					}
					break;
				case GROUP_FILL:
					braces = calculateBraces(braces, line);
					if (braces == -1) {
						log.trace("Stopped filling squad {} in line {}", nextSquad.getName(), lineNumber);
						braces = 0;
						step = step.next();
					}
					if (line.contains(DESCRIPTION)) {
						assert nextSquad != null; //This step is only reached, if the squad has been initialized
						readSlot(getDescriptionText(line), nextSquad);
					}
					break;
				case GROUP_END:
					braces = calculateBraces(braces, line);
					if (braces == -1) {
						slotList.add(nextSquad);
						log.trace("Group definition end in line {}", lineNumber);

						//Reset all values
						braces = 0;
						nextSquad = null;
						step = step.next();
					}
					break;
			}
		}

		return slotList;
	}

	/**
	 * Adds one if open curly brace was found, subtracts one if a closing curly brace was found
	 *
	 * @param braces current brace count
	 * @param line   to read
	 * @return new brace count
	 */
	private static int calculateBraces(int braces, @NonNull String line) {
		if (line.contains(CURLY_BRACE_OPEN)) {
			braces++;
		}
		if (line.contains(CURLY_BRACE_CLOSE)) {
			braces--;
		}
		return braces;
	}

	/**
	 * @param line complete line
	 * @return text inside outer quotation marks
	 */
	private static String getDescriptionText(String line) {
		return line.substring(line.indexOf(QUOTE_MARK) + 1, line.lastIndexOf(QUOTE_MARK));
	}

	private static final Pattern DIGIT = Pattern.compile("^#?\\d+");
	private static final Pattern END_OF_STRING_AND_LINE = Pattern.compile("\";$");

	/**
	 * Parses the {@link Slot} definition from the given string and adds it to the squad
	 *
	 * @param s     to parse slot from. Include number and name
	 * @param squad to add slot to
	 */
	private void readSlot(String s, Squad squad) {
		final Matcher matcher = DIGIT.matcher(s);

		Slot slot;
		if (matcher.find()) {
			final String slotNumber = matcher.group();
			final int end = END_OF_STRING_AND_LINE.matcher(s).find() ? s.indexOf("\";") : s.length();
			final String slotName = s.substring(s.indexOf(slotNumber) + slotNumber.length(), end).trim();

			slot = Slot.builder().number(Integer.parseInt(removeNonDigitCharacters(slotNumber))).name(slotName).squad(squad).build();
		} else {
			slot = Slot.builder().name(s.trim()).squad(squad).build();
		}
		log.trace("Added slot '{}'", slot.getName());
		squad.getSlotList().add(slot);
	}

	enum ReadStep {
		NONE,
		GROUP_SEARCH,
		GROUP_FOUND,
		GROUP_FILL,
		GROUP_END;

		private static final ReadStep[] values = values();

		public ReadStep next() {
			final ReadStep nextStep = values[(ordinal() + 1) % values.length];
			log.trace("Next step {}", nextStep.name());
			return nextStep;
		}
	}
}
