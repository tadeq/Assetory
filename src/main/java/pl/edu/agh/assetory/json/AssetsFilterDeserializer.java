package pl.edu.agh.assetory.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import pl.edu.agh.assetory.model.AssetsFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pl.edu.agh.assetory.model.AssetsFilter.*;

public class AssetsFilterDeserializer extends JsonDeserializer<AssetsFilter> {
    @Override
    public AssetsFilter deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode mainNode = parser.getCodec().readTree(parser);
        AssetsFilterBuilder builder = AssetsFilter.builder();

        String mainCategoryId = (mainNode.get(MAIN_CATEGORY_ID_FIELD)).asText();
        builder.mainCategoryId(mainCategoryId);

        JsonNode filters = mainNode.get(FILTERS_FIELD);
        Map<String, List<String>> filtersMap = mapper.readerFor(new TypeReference<Map<String, List<String>>>() {
        }).readValue(filters);
        filtersMap.remove(NAME_FIELD);
        filtersMap.remove(CATEGORY_ID_FIELD);
        builder.filters(filtersMap);

        ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {
        });
        Optional<JsonNode> nameNode = Optional.ofNullable(filters.get(NAME_FIELD));
        Optional<JsonNode> categoryIdsNode = Optional.ofNullable(filters.get(CATEGORY_ID_FIELD));
        if (nameNode.isPresent()) {
            builder.name(reader.readValue(nameNode.get()));
        }
        if (categoryIdsNode.isPresent()) {
            builder.categoryId(reader.readValue(categoryIdsNode.get()));
        }
        return builder.build();
    }
}
