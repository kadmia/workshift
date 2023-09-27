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

public class ShopTest {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Test
	void serializeToJson() throws IOException, URISyntaxException {
		Shop shop = new Shop(1, "Adam's shop");
		
		final File expectedFile = new File(getClass().getResource("/fixtures/shop.json").toURI());
		final String expected = new String(Files.readAllBytes(expectedFile.toPath()));
		final String actual = MAPPER.writeValueAsString(shop);
		
		assertEquals("Serialization is not equal to the file", expected, actual);
	}
	
	@Test 
	void deserializeFromJson() throws URISyntaxException, StreamReadException, DatabindException, IOException {
		Shop expected = new Shop(1, "Adam's shop");
		Shop actual = MAPPER.readValue(getClass().getResource("/fixtures/shop.json"), Shop.class);
		
		assertEquals("Deserialization not equal to object", expected.getId(), actual.getId());
		assertEquals("Deserialization not equal to object", expected.getName(), actual.getName());
	}
}
