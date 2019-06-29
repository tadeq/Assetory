package pl.edu.agh.assetory.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Document(indexName = "assetory", type = "category")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Category extends Entity {
    private String path;
    private List<String> attributes;
    public static String PATH_SEPARATOR = ":";
    public static String PATH_FIELD_KEY = "path";
    public static String ATTRIBUTES_FIELD_KEY = "attributes";
    public Category(String id, String name, String path, List<String> attributes) {
        super(id, name);
        this.path = path;
        this.attributes = attributes;
    }
}


