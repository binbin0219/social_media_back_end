package my_social_media_project_backend.demo.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode convertStringToJsonNode(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("Error converting String to JsonNode", e);
        }
    }
}

