package pl.edu.agh.assetory.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.stereotype.Component;
import pl.edu.agh.assetory.model.attributes.AssetAttribute;
import pl.edu.agh.assetory.model.attributes.AttributeType;
import pl.edu.agh.assetory.model.attributes.CategoryAttribute;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
@Document(indexName = "assetory", type = "asset")
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
public class Asset extends DBEntity {
    public static final String CATEGORY_ID_FIELD_KEY = "categoryId";
    public static final String NAME_FIELD_KEY = "name";

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Keyword)
    @Setter
    private String categoryId;

    @Getter(AccessLevel.NONE)
    private List<AssetAttribute> attributes = Lists.newArrayList();

    @Getter(AccessLevel.NONE)
    private Set<String> relatedAssetsIds = Sets.newHashSet();

    public Asset(String id, String name, String categoryId, List<AssetAttribute> attributes) {
        super(id);
        this.name = name;
        this.categoryId = categoryId;
        this.attributes = attributes;
    }

    public Asset(String id, String name, String categoryId, List<AssetAttribute> attributes, Set<String> relatedAssetsIds) {
        this(id, name, categoryId, attributes);
        this.relatedAssetsIds = relatedAssetsIds;
    }

    public List<AssetAttribute> getAttributes() {
        return ImmutableList.copyOf(attributes);
    }

    public Set<String> getRelatedAssetsIds() {
        return ImmutableSet.copyOf(relatedAssetsIds);
    }

    public void addAttribute(AssetAttribute attribute) {
        this.attributes.add(attribute);
    }

    public void removeAttribute(AssetAttribute attribute) {
        this.attributes.remove(attribute);
    }

    public void removeAttribute(String attributeName) {
        this.attributes.stream()
                .filter(attribute -> attribute.getAttribute().getName().equals(attributeName))
                .findFirst().ifPresent(attribute -> this.attributes.remove(attribute));
    }

    public void addRelatedAssetId(String assetId) {
        this.relatedAssetsIds.add(assetId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        Asset asset;

        public Builder() {
            asset = new Asset();
        }

        public Builder from(Asset asset) {
            this.asset = asset;
            return this;
        }

        public Builder name(String name) {
            this.asset.name = name;
            return this;
        }

        public Builder categoryId(String categoryId) {
            this.asset.categoryId = categoryId;
            return this;
        }

        public Builder addAttribute(AttributeType type, String name, String value) {
            this.asset.attributes.add(new AssetAttribute(new CategoryAttribute(type, name), value));
            return this;
        }

        public Builder addAttributes(Collection<AssetAttribute> attributes) {
            this.asset.attributes.addAll(attributes);
            return this;
        }

        public Builder addRelatedAsset(String assetId) {
            this.asset.relatedAssetsIds.add(assetId);
            return this;
        }

        public Builder addRelatedAssets(Collection<String> assetIds) {
            this.asset.relatedAssetsIds.addAll(assetIds);
            return this;
        }

        public Asset build() {
            return this.asset;
        }
    }
}