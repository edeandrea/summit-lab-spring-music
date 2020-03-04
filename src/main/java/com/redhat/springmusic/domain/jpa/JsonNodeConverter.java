package com.redhat.springmusic.domain.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

@Converter
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonNodeConverter.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@SneakyThrows(JsonProcessingException.class)
	@Override
	public String convertToDatabaseColumn(JsonNode attribute) {
		String json = OBJECT_MAPPER.writeValueAsString(attribute);
		LOGGER.debug("Converted JsonNode({}) to database column json ({})", attribute, json);

		return json;
	}

	@SneakyThrows(JsonProcessingException.class)
	@Override
	public JsonNode convertToEntityAttribute(String dbData) {
		JsonNode obj = OBJECT_MAPPER.readTree(dbData);
		LOGGER.debug("Converted column json ({}) to JsonNode({})", dbData, obj);

		return obj;
	}
}
