package pl.edu.agh.assetory.model.update;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.edu.agh.assetory.model.Category;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdate {
    private Category category;
    private Map<String, String> attributeChanges = Maps.newHashMap();
}
