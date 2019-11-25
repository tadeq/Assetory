package pl.edu.agh.assetory.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.assetory.export.AssetsCSVExporter;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.model.Category;
import pl.edu.agh.assetory.service.AssetsService;
import pl.edu.agh.assetory.service.CategoriesService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class ExportController {
    private AssetsService assetsService;
    private AssetsCSVExporter assetsCsvExporter;
    private CategoriesService categoriesService;

    @Autowired
    public ExportController(AssetsService assetsService, AssetsCSVExporter assetsCsvExporter, CategoriesService categoriesService) {
        this.assetsService = assetsService;
        this.assetsCsvExporter = assetsCsvExporter;
        this.categoriesService = categoriesService;
    }

    @GetMapping("export-assets")
    @ApiOperation(value = "returns assets.csv - all assets exported to csv file")
    public ResponseEntity<?> exportAllAssets() throws IOException {
        String filename = "assets.csv";
        List<Asset> assets = assetsService.getAllAssets();
        Map<String, String> categoriesNames = StreamSupport.stream(categoriesService.getAllCategories().spliterator(), false)
                .collect(Collectors.toMap(Category::getId, Category::getName));
        File csvFile = assetsCsvExporter.exportAssets(filename, assets, categoriesNames);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(csvFile));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(csvFile.length())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}
