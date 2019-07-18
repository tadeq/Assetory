package pl.edu.agh.assetory.model;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Component;
import pl.edu.agh.assetory.model.attributes.AttributeType;
import pl.edu.agh.assetory.model.attributes.CategoryAttribute;

import java.util.Collection;
import java.util.List;

@Component
@Document(indexName = "assetory", type = "category")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Category extends DBEntity {
    public static final String ATTRIBUTES_FIELD_KEY = "attributeNames";
    public static final String PARENT_ID_FIELD_KEY = "parentCategoryId";
    public static final String SUBCATEGORIES_IDS_FIELD_KEY = "subcategoryIds";
    public static final String NAME_FIELD_KEY = "name";
    private String name;
    private List<CategoryAttribute> additionalAttributes = Lists.newArrayList();
    private String parentCategoryId;
    private List<String> subcategoryIds = Lists.newArrayList();

    public Category(String id, String name, List<CategoryAttribute> additionalAttributes, String parentCategoryId, List<String> subcategoryIds) {
        super(id);
        this.name = name;
        this.additionalAttributes = additionalAttributes;
        this.parentCategoryId = parentCategoryId;
        this.subcategoryIds = subcategoryIds;
    }

    public void addSubcategory(String subcategoryId) {
        this.subcategoryIds.add(subcategoryId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        Category category;

        public Builder() {
            category = new Category();
        }

        public Builder from(Category category) {
            this.category = category;
            return this;
        }

        public Builder name(String name) {
            this.category.name = name;
            return this;
        }

        public Builder parentCategoryId(String parentCategoryId) {
            this.category.parentCategoryId = parentCategoryId;
            return this;
        }

        public Builder addAttribute(String name, AttributeType type) {
            this.category.additionalAttributes.add(new CategoryAttribute(type, name));
            return this;
        }

        public Builder addAttributes(Collection<CategoryAttribute> attributes) {
            this.category.additionalAttributes.addAll(attributes);
            return this;
        }

        public Builder addSubcategoryId(String id) {
            this.category.subcategoryIds.add(id);
            return this;
        }

        public Builder addSubcategoryIds(Collection<String> ids) {
            this.category.subcategoryIds.addAll(ids);
            return this;
        }

        public Category build() {
            return this.category;
        }
    }
}


