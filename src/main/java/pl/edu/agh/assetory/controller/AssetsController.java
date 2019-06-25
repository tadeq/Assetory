package pl.edu.agh.assetory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.service.AssetsService;

@RestController
@RequestMapping(value = "/assets")
public class AssetsController {

    private AssetsService assetsService;

    @Autowired
    public AssetsController(AssetsService assetsService) {
        this.assetsService = assetsService;
    }

    @PostMapping
    public ResponseEntity<?> addAsset(@RequestBody Asset newAsset) {
        return ResponseEntity.ok(assetsService.addAsset(newAsset));
    }

    @GetMapping
    public ResponseEntity<?> getAllAssets() {
        return ResponseEntity.ok(assetsService.getAllAssets());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getAsset(@PathVariable String id) {
        return ResponseEntity.ok(assetsService.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable String id) {
        assetsService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<?> updateAsset(@RequestBody Asset asset) {
        return ResponseEntity.ok(assetsService.updateAsset(asset));
    }
}