package pl.edu.agh.assetory.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Document(indexName = "assetory", type = "asset")
public class Asset {

    @Id
    private String id;
    private String name;
    private Map<String, String> fields;

    public Asset() {
    }

    public Asset(String id, String name, Map<String, String> fields) {
        this.id = id;
        this.name = name;
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "Asset [id=" + id + ", name=" + name + ", fields=" + fields.toString() + "]";
    }
}