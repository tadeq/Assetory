package pl.edu.agh.assetory.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.*;
import org.springframework.stereotype.Component;
import pl.edu.agh.assetory.model.attributes.AttributeType;
import pl.edu.agh.assetory.model.attributes.CategoryAttribute;

import java.util.Collection;
import java.util.List;

@Component
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
public class Category extends DBEntity {
    public static final String ATTRIBUTES_FIELD_KEY = "attributeNames";
    public static final String PARENT_ID_FIELD_KEY = "parentCategoryId";
    public static final String SUBCATEGORIES_IDS_FIELD_KEY = "subcategoryIds";
    public static final String NAME_FIELD_KEY = "name";

    private String name;

    @Getter(AccessLevel.NONE)
    private List<CategoryAttribute> additionalAttributes = Lists.newArrayList();

    @Setter
    private String parentCategoryId;

    @Getter(AccessLevel.NONE)
    private List<String> subcategoryIds = Lists.newArrayList();

    public Category(String id, String name, List<CategoryAttribute> additionalAttributes, String parentCategoryId, List<String> subcategoryIds) {
        super(id);
        this.name = name;
        this.additionalAttributes = additionalAttributes;
        this.parentCategoryId = parentCategoryId;
        this.subcategoryIds = subcategoryIds;
    }

    public List<CategoryAttribute> getAdditionalAttributes() {
        return ImmutableList.copyOf(additionalAttributes);
    }

    public List<String> getSubcategoryIds() {
        return ImmutableList.copyOf(subcategoryIds);
    }

    public void addAttribute(CategoryAttribute attribute) {
        this.additionalAttributes.add(attribute);
    }

    public void addSubcategoryId(String subcategoryId) {
        this.subcategoryIds.add(subcategoryId);
    }

    public void addSubcategoryIds(Collection<String> categoryIds) {
        this.subcategoryIds.addAll(categoryIds);
    }

    public void removeSubcategoryId(String subcategoryId) {
        this.subcategoryIds.remove(subcategoryId);
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


