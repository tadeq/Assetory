package pl.edu.agh.assetory.model;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Entity {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    private String name;
}
