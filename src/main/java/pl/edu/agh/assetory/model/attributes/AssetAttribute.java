package pl.edu.agh.assetory.model.attributes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssetAttribute {
    private CategoryAttribute attribute;
    private String value;
}

