package pl.edu.agh.assetory.model.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssetAttributesUpdate {
    private String id;
    private Map<String, String> attributes;
}
