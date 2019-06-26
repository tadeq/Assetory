package pl.edu.agh.assetory.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Document(indexName = "assetory", type = "asset")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Asset extends Entity {
    public static String CATEGORY_FIELD_KEY = "category";
    public static String ATTRIBUTES_MAP_FIELD_KEY = "attributesMap";
    private String category;
    private Map<String, String> attributesMap;

    public Asset(String id, String name, String category, Map<String, String> attributesMap) {
        super(id, name);
        this.category = category;
        this.attributesMap = attributesMap;
    }
}