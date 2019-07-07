package pl.edu.agh.assetory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetAttribute {
    private AttributeType type;
    private String name;
    private String value;
}

