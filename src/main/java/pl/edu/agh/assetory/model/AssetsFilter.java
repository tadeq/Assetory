package pl.edu.agh.assetory.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class AssetsFilter {
    private List<String> name;
    private String treeCategory;
    private List<String> categoryId;
    private Map<String, List<String>> attributesMap;
    private List<String> localisation;
    private List<String> backup;
    private List<String> license;
    private List<BigDecimal> value;
    private List<String> owner;
    private List<String> user;

    public AssetsFilter(List<String> name, String treeCategory, List<String> categoryId, Map<String, List<String>> attributesMap, List<String> localisation, List<String> backup, List<String> license, List<BigDecimal> value, List<String> owner, List<String> user) {
        this.name = name;
        this.treeCategory = treeCategory;
        this.categoryId = categoryId;
        this.attributesMap = attributesMap;
        this.localisation = localisation;
        this.backup = backup;
        this.license = license;
        this.value = value;
        this.owner = owner;
        this.user = user;
    }
}
