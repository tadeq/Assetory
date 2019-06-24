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
    private List<String> path;
    private List<String> attributes;

    public Category(String id, String name, List<String> path, List<String> attributes) {
        super(id, name);
        this.path = path;
        this.attributes = attributes;
    }
}


