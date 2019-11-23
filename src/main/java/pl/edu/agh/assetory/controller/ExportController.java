package pl.edu.agh.assetory.controller;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.assetory.export.CSVFileWriter;
import pl.edu.agh.assetory.service.AssetsService;
import pl.edu.agh.assetory.service.CategoriesService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class ExportController {
    private AssetsService assetsService;
    private CategoriesService categoriesService;
    private CSVFileWriter csvFileWriter;

    @Autowired
    public ExportController(AssetsService assetsService, CategoriesService categoriesService, CSVFileWriter csvFileWriter) {
        this.assetsService = assetsService;
        this.categoriesService = categoriesService;
        this.csvFileWriter = csvFileWriter;
    }

    @GetMapping("export-assets")
    @ApiOperation(value = "returns assets.csv - all assets exported to csv file")
    public ResponseEntity<?> exportAllAssets() throws IOException {
        String filename = "assets.csv";
        List<List<String>> data = Lists.newArrayList(
                Lists.newArrayList("categoryId", "name"),
                Lists.newArrayList("1234", "asset1"));
        File csvFile = csvFileWriter.createCSVFile(filename, data);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(csvFile));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(csvFile.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }
}
