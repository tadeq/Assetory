package pl.edu.agh.assetory.model;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Component;

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
    private String name;
    private List<String> attributeNames = Lists.newArrayList();
    private String parentCategoryId;
    private List<String> subcategoryIds;

    public Category(String id, String name, List<String> attributeNames, String parentCategoryId, List<String> subcategoryIds) {
        super(id);
        this.name = name;
        this.attributeNames = attributeNames;
        this.parentCategoryId = parentCategoryId;
        this.subcategoryIds = subcategoryIds;
    }

    public void addSubcategory(String subcategoryId) {
        this.subcategoryIds.add(subcategoryId);
    }

    public static class Builder {
        Category category;

        public Builder name(String name) {
            this.category.name = name;
            return this;
        }

        public Builder parentCategoryId(String parentCategoryId) {
            this.category.parentCategoryId = parentCategoryId;
            return this;
        }

        public Builder addAttributeName(String attributeName) {
            this.category.attributeNames.add(attributeName);
            return this;
        }

        public Builder addAttributeNames(Collection<String> attributeNames) {
            this.category.attributeNames.addAll(attributeNames);
            return this;
        }

        public Category build() {
            return this.category;
        }
    }
}


