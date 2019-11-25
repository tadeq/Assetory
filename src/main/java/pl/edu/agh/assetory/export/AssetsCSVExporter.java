package pl.edu.agh.assetory.export;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.model.attributes.AssetAttribute;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AssetsCSVExporter {
    @Autowired
    public AssetsCSVExporter() {
    }

    public File exportAssets(String filename, List<Asset> assets, Map<String, String> categoryNames) throws FileNotFoundException {
        File csvFile = new File(filename);
        List<String> attributesHeaders = getAttributesHeaders(assets);
        List<String> headers = Lists.newLinkedList();
        headers.add("Name");
        headers.add("Category");
        headers.addAll(attributesHeaders);
        try (PrintWriter printWriter = new PrintWriter(csvFile)) {
            printWriter.println(String.join(",", headers));
            assets.forEach(asset -> printWriter.println(String.join(",", getAssetAttributes(asset, attributesHeaders, categoryNames))));
        }
        return csvFile;
    }

    private List<String> getAssetAttributes(Asset asset, List<String> attributesHeaders, Map<String, String> categoryNames) {
        List<String> attributes = Lists.newLinkedList();
        attributes.add(asset.getName());
        String categoryName = Optional.ofNullable(categoryNames.get(asset.getCategoryId())).orElse("");
        attributes.add(categoryName);
        attributesHeaders.forEach(header -> {
            String attributeValue = asset.getAttribute(header).map(AssetAttribute::getValue).orElse("");
            attributes.add(attributeValue);
        });
        return attributes;
    }

    private List<String> getAttributesHeaders(List<Asset> assets) {
        return assets.stream()
                .map(Asset::getAttributesNames)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}