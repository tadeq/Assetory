package pl.edu.agh.assetory.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Document(indexName = "assetory", type = "asset")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Asset extends Entity {
    public static String CATEGORY_FIELD_KEY = "category";
    public static String ATTRIBUTES_MAP_FIELD_KEY = "attributesMap";
    public static String LOCALISATION_FIELD_KEY = "localisation";
    public static String BACKUP_FIELD_KEY = "backup";
    public static String LICENSE_FIELD_KEY = "license";
    public static String VALUE_FIELD_KEY = "value";
    public static String OWNER_FIELD_KEY = "owner";
    public static String USER_FIELD_KEY = "user";
    private String category;
    private Map<String, String> attributesMap;
    private String localisation;
    private String backup;
    private String license;
    private BigDecimal value;
    private String owner;
    private String user;

    public Asset(String id, String name, String category, Map<String, String> attributesMap, String localisation, String backup, String license, BigDecimal value, String owner, String user) {
        super(id, name);
        this.category = category;
        this.attributesMap = attributesMap;
        this.localisation = localisation;
        this.backup = backup;
        this.license = license;
        this.value = value;
        this.owner = owner;
        this.user = user;
    }

    public boolean hasAllUpdatedAttributes(Asset update) {
        return this.attributesMap.keySet().containsAll(update.getAttributesMap().keySet());
    }

    public void updateAttributes(Map<String, String> attributes) {
        this.attributesMap.putAll(attributes);
    }
}