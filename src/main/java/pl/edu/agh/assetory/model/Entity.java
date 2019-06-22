package pl.edu.agh.assetory.model;

import org.springframework.data.annotation.Id;

public abstract class Entity {
    @Id
    private String id;
    private String name;
    public Entity(){}
    public Entity(String id, String name) {
        this.id = id;
        this.name = name;
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

}
