package pl.edu.agh.assetory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DBEntity {
    public static final String NAME_FIELD_KEY = "name";
    public static final String ID_FIELD_KEY = "id";
    //    @Setter(AccessLevel.NONE)
    private String id;
}
