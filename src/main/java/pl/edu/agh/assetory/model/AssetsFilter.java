package pl.edu.agh.assetory.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import pl.edu.agh.assetory.json.AssetsFilterDeserializer;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = AssetsFilterDeserializer.class)
public class AssetsFilter {
    public static final String NAME_FIELD = "name";
    public static final String MAIN_CATEGORY_ID_FIELD = "mainCategoryId";
    public static final String CATEGORY_ID_FIELD = "categoryId";
    public static final String FILTERS_FIELD = "filters";

    private String mainCategoryId;
    private List<String> name;
    @Setter
    private List<String> categoryId;
    private Map<String, List<String>> filters;
}
