package pl.edu.agh.assetory.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import pl.edu.agh.assetory.json.AssetsFilterDeserializer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = AssetsFilterDeserializer.class)
public class AssetsFilter {
    public static final String MAIN_CATEGORY_ID_FIELD_NAME = "mainCategoryId";
    public static final String CATEGORY_ID_FIELD_NAME = "categoryId";
    public static final String FILTERS_FIELD_NAME = "filters";

    private String mainCategoryId;
    @Setter
    private Collection<String> categoryId;
    private Map<String, List<String>> filters;
}
