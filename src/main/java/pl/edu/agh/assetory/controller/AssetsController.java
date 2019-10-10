package pl.edu.agh.assetory.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.model.AssetsFilter;
import pl.edu.agh.assetory.service.AssetsService;
import pl.edu.agh.assetory.service.CategoriesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        return ResponseEntity.ok(assetsService.getById(id));
    }

    @GetMapping(value = "/ids")
    @ApiOperation(value = "returns asset by given list of ids",
            response = Asset.class,
            responseContainer = "List")
    public ResponseEntity<?> getAssets(@RequestParam List<String> ids) {
        return ResponseEntity.ok(assetsService.getByIds(ids));
    }

    @PostMapping(value = "/name")
    @ApiOperation(value = "returns asset with given name",
            response = Asset.class)
    public ResponseEntity<?> getAssetByName(@RequestBody String name) {
        return ResponseEntity.ok(assetsService.getByName(name));
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
            notes = "asset is recognized by id, name is mandatory, other attributes will be removed if not provided",
            response = Asset.class)
    public ResponseEntity<?> updateAsset(@RequestBody Asset asset) {
        if (asset.getId() == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(assetsService.updateAsset(asset));
    }

    @PostMapping(value = "/filter")
    @ApiOperation(value = "Filters all assets",
            notes = "Filter all assets based on fields given in body. These fields are: id, name, categoryId, attributesMap")
    public ResponseEntity<?> filterAssetsByFields(@RequestBody AssetsFilter assetsFilter) {
        if (assetsFilter.getMainCategoryId() == null) {
            return ResponseEntity.badRequest().build();
        } else {
            Set<String> matchingCategoryIds = Optional.ofNullable(assetsFilter.getCategoryId())
                    .map(ids -> ids.stream()
                            .map(categoriesService::getMatchingCategoryIds)
                            .flatMap(Set::stream)
                            .collect(Collectors.toSet()))
                    .orElseGet(() -> categoriesService.getMatchingCategoryIds(assetsFilter.getMainCategoryId()));
            assetsFilter.setCategoryId(new ArrayList<>(matchingCategoryIds));
            return ResponseEntity.ok(assetsService.filterAssetsByFields(assetsFilter));
        }

    }
}