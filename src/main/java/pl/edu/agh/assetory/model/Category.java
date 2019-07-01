package pl.edu.agh.assetory.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
@Document(indexName = "assetory", type = "category")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Category extends DBEntity {
    public static String ATTRIBUTES_FIELD_KEY = "attributes";
    public static String PARENT_ID_FIELD_KEY = "parentId";
    public static String SUBCATEGORIES_IDS_FIELD_KEY = "subcategoriesIds";
    private List<String> attributes = new LinkedList<>();
    private String parentId;
    private List<String> subcategoriesIds;

    public Category(String id, String name, List<String> attributes) {
        this(id, name, attributes, null);
    }

    public Category(String id, String name, List<String> attributes, String parentId) {
        super(id, name);
        this.attributes = attributes;
        this.parentId = parentId;
        this.subcategoriesIds = new LinkedList<>();
    }

    public void addSubcategory(String subcategoryId) {
        this.subcategoriesIds.add(subcategoryId);
    }
}


