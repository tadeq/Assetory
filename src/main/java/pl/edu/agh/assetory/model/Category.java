package pl.edu.agh.assetory.model;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Document(indexName = "assetory", type = "category")
public class Category extends Entity {
    private String path;
    private List<String> mandatoryFields;

    public Category(){}
    public Category(String id, String name, String path, List<String> mandatoryFields) {
        super(id, name);
        this.path = path;
        this.mandatoryFields = mandatoryFields;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getMandatoryFields() {
        return mandatoryFields;
    }

    public void setMandatoryFields(List<String> mandatoryFields) {
        this.mandatoryFields = mandatoryFields;
    }
}
