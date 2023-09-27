package com.adambritt.workshift.api;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ShiftTest {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	{
		 MAPPER.registerModule(new JavaTimeModule());
	}

	@Test
	void serializeToJson() throws IOException, URISyntaxException {
		Shift shift = new Shift(1,
				ZonedDateTime.parse("2023-09-23T10:00:00.000+02:00", DateTimeFormatter.ISO_ZONED_DATE_TIME),
				ZonedDateTime.parse("2023-09-23T18:00:00.000+02:00", DateTimeFormatter.ISO_ZONED_DATE_TIME), null,
				null);

		final File expectedFile = new File(getClass().getResource("/fixtures/shift.json").toURI());
		final String expected = new String(Files.readAllBytes(expectedFile.toPath()));
		final String actual = MAPPER.writeValueAsString(shift);

		assertEquals("Serialization is not equal to the file", expected, actual);
	}

	@Test
	void deserializeFromJson() throws URISyntaxException, StreamReadException, DatabindException, IOException {
		Shift expected = new Shift(1,
				ZonedDateTime.parse("2023-09-23T10:00:00.000+02:00", DateTimeFormatter.ISO_ZONED_DATE_TIME),
				ZonedDateTime.parse("2023-09-23T18:00:00.000+02:00", DateTimeFormatter.ISO_ZONED_DATE_TIME), null,
				null);
		Shift actual = MAPPER.readValue(getClass().getResource("/fixtures/shift.json"), Shift.class);

		assertEquals("Deserialization not equal to object", expected.getId(), actual.getId());
	}
}
