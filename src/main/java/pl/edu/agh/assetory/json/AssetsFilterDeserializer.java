package pl.edu.agh.assetory.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.google.common.collect.Lists;
import pl.edu.agh.assetory.model.AssetsFilter;
import pl.edu.agh.assetory.repository.CategoriesRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static pl.edu.agh.assetory.model.AssetsFilter.*;

public class AssetsFilterDeserializer extends JsonDeserializer<AssetsFilter> {
    private CategoriesRepository categoriesRepository;

    @Override
    public AssetsFilter deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode mainNode = parser.getCodec().readTree(parser);
        String maincategoryId = (mainNode.get(MAIN_CATEGORY_ID_FIELD_NAME)).asText();
        JsonNode filters = mainNode.get(FILTERS_FIELD_NAME);
        ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {
        });
        Map<String, List<String>> filtersMap = mapper.readerFor(new TypeReference<Map<String, List<String>>>() {
        }).readValue(filters);
        filtersMap.remove(CATEGORY_ID_FIELD_NAME);
        JsonNode categoryIdsNode = filters.get(CATEGORY_ID_FIELD_NAME);
        if (categoryIdsNode != null) {
            List<String> categoryIds = reader.readValue(categoryIdsNode);
            return new AssetsFilter(maincategoryId, categoryIds, filtersMap);
        }
        //TODO fix
        return new AssetsFilter(maincategoryId, Lists.newArrayList(), filtersMap);
    }
}
