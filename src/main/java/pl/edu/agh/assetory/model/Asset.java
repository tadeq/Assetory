package pl.edu.agh.assetory.model;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Document(indexName = "assetory", type = "asset")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Asset extends DBEntity {
    public static final String CATEGORY_ID_FIELD_KEY = "categoryId";
    public static final String LOCALISATION_FIELD_KEY = "localisation";
    public static final String BACKUP_FIELD_KEY = "backup";
    public static final String LICENSE_FIELD_KEY = "license";
    public static final String VALUE_FIELD_KEY = "value";
    public static final String OWNER_FIELD_KEY = "owner";
    public static final String USER_FIELD_KEY = "user";
    private String name;
    private String categoryId;
    private List<AssetAttribute> attributes;

    public Asset(String id, String name, String categoryId, List<AssetAttribute> attributes) {
        super(id);
        this.name = name;
        this.categoryId = categoryId;
        this.attributes = attributes;
    }

    public boolean hasAllUpdatedAttributes(Asset update) {
        return getAttributeNames().containsAll(update.getAttributeNames());
    }

    public void updateAttributes(List<AssetAttribute> attributes) {
        this.attributes = Lists.newArrayList(attributes);
    }

    private List<String> getAttributeNames() {
        return this.attributes.stream()
                .map(AssetAttribute::getName)
                .collect(Collectors.toList());
    }

    public static class Builder {
        Asset asset;

        public Builder name(String name) {
            this.asset.name = name;
            return this;
        }

        public Builder categoryId(String categoryId) {
            this.asset.categoryId = categoryId;
            return this;
        }

        public Builder addAttribute(AttributeType attributeType, String attributeName, String value) {
            this.asset.attributes.add(new AssetAttribute(attributeType, attributeName, value));
            return this;
        }

        public Builder addAttributes(List<AssetAttribute> attributes) {
            this.asset.attributes.addAll(attributes);
            return this;
        }

        public Asset build() {
            return this.asset;
        }
    }
}