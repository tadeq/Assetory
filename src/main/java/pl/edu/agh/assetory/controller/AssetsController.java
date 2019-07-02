package pl.edu.agh.assetory.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.service.AssetsService;
import pl.edu.agh.assetory.service.CategoriesService;

@RestController
@RequestMapping(value = "/assets")
public class AssetsController {

    private AssetsService assetsService;
    private CategoriesService categoriesService;

    @Autowired
    public AssetsController(AssetsService assetsService, CategoriesService categoriesService) {
        this.assetsService = assetsService;
        this.categoriesService = categoriesService;
    }

    @PostMapping
    @ApiOperation(value = "adds new asset",
            response = Asset.class)
    public ResponseEntity<?> addAsset(@RequestBody Asset newAsset) {
        return ResponseEntity.ok(assetsService.addAsset(newAsset));
    }

    @GetMapping
    @ApiOperation(value = "returns all assets",
            response = Asset.class,
            responseContainer = "List")
    public ResponseEntity<?> getAllAssets() {
        return ResponseEntity.ok(assetsService.getAllAssets());
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "returns asset with given id",
            response = Asset.class)
    public ResponseEntity<?> getAsset(@PathVariable String id) {
        return ResponseEntity.of(assetsService.getById(id));
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "deletes asset with given id")
    public ResponseEntity<?> deleteAsset(@PathVariable String id) {
        return assetsService.getById(id)
                .map(asset -> {
                    assetsService.deleteAsset(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    @ApiOperation(value = "updates asset given in body",
            notes = "asset is recognized by id, all attributes from before the operation have to be provided",
            response = Asset.class)
    public ResponseEntity<?> updateAsset(@RequestBody Asset update) {
        if (update.getId() == null) return ResponseEntity.badRequest().build();
        return assetsService.getById(update.getId())
                .map(asset -> {
                    if (update.getCategoryId() != null) {
                        if (categoriesService.findById(update.getCategoryId()).isPresent()) {
                            asset.setCategoryId(update.getCategoryId());
                        } else {
                            return ResponseEntity.badRequest().build();
                        }
                    }
                    if (update.getAttributesMap() != null) {
                        if (!asset.hasAllUpdatedAttributes(update)) {
                            asset.updateAttributes(update.getAttributesMap());
                        } else {
                            return ResponseEntity.badRequest().build();
                        }
                    }
                    if (update.getName() != null) asset.setName(update.getName());
                    if (update.getLocalisation() != null) asset.setLocalisation(update.getLocalisation());
                    if (update.getBackup() != null) asset.setBackup(update.getBackup());
                    if (update.getLicense() != null) asset.setLicense(update.getLicense());
                    if (update.getValue() != null) asset.setValue(update.getValue());
                    if (update.getOwner() != null) asset.setOwner(update.getOwner());
                    if (update.getUser() != null) asset.setUser(update.getUser());
                    return ResponseEntity.ok(assetsService.updateAsset(asset));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/filter")
    @ApiOperation(value = "Filters all assets",
            notes = "Filter all assets based on fields given in body. These fields are: id, name, categoryId, attributesMap")
    public ResponseEntity<?> filterAssetsByFields(@RequestBody Asset assetTemplate) {
        return ResponseEntity.ok(assetsService.filterAssetsByFields(assetTemplate));
    }
}