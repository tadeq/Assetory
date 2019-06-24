package pl.edu.agh.assetory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Entity {
    @Id
    private String id;
    private String name;
}
