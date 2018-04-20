package net.kaciras.blog.infrastructure.message;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

@Data
public final class CommonMessageWrapper {
	private String type;
	private List<String> ancestors;
	private JsonNode data;
}
