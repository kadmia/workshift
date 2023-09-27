package com.adambritt.workshift.api;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserTest {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@Test
	void serializeToJson() throws IOException, URISyntaxException {
		User user = new User(1, "Adam");
		
		final File expectedFile = new File(getClass().getResource("/fixtures/user.json").toURI());
		final String expected = new String(Files.readAllBytes(expectedFile.toPath()));
		final String actual = MAPPER.writeValueAsString(user);
		
		assertEquals("Serialization is not equal to the file", expected, actual);
	}
	
	@Test 
	void deserializeFromJson() throws URISyntaxException, StreamReadException, DatabindException, IOException {
		User expected = new User(1, "Adam");
		Shop actual = MAPPER.readValue(getClass().getResource("/fixtures/user.json"), Shop.class);
		
		assertEquals("Deserialization not equal to object", expected.getId(), actual.getId());
		assertEquals("Deserialization not equal to object", expected.getName(), actual.getName());
	}
}
