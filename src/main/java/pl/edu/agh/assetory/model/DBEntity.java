package pl.edu.agh.assetory.model;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DBEntity {
    public static String NAME_FIELD_KEY = "name";
    public static String ID_FIELD_KEY = "id";
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    private String name;
}
