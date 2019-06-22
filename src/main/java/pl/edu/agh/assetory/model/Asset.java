package pl.edu.agh.assetory.model;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Document(indexName = "assetory", type = "asset")
public class Asset extends Entity {
    private String category;
    private Map<String, String> fields;

    public Asset(){}
    public Asset(String id, String name, String category, Map<String, String> fields) {
        super(id, name);
        this.category = category;
        this.fields = fields;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}