package pl.edu.agh.assetory.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import pl.edu.agh.assetory.model.AssetsFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static pl.edu.agh.assetory.model.AssetsFilter.*;

public class AssetsFilterDeserializer extends JsonDeserializer<AssetsFilter> {
    @Override
    public AssetsFilter deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode mainNode = parser.getCodec().readTree(parser);
        String maincategoryId = (mainNode.get(MAIN_CATEGORY_ID_FIELD_NAME)).asText();
        JsonNode filters = mainNode.get(FILTERS_FIELD_NAME);
        ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {
        });
        JsonNode categoryIdsNode = filters.get(CATEGORY_IDS_FIELD_NAME);
        List<String> categoryIds = reader.readValue(categoryIdsNode);
        Map<String, List<String>> filtersMap = mapper.readerFor(new TypeReference<Map<String, List<String>>>() {
        }).readValue(filters);
        filtersMap.remove(CATEGORY_IDS_FIELD_NAME);
        return new AssetsFilter(maincategoryId, categoryIds, filtersMap);
    }
}
