package pl.edu.agh.assetory.controller;

import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.DocWriteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.model.AssetsFilter;
import pl.edu.agh.assetory.model.update.AssetAttributesUpdate;
import pl.edu.agh.assetory.service.AssetsService;
import pl.edu.agh.assetory.service.CategoriesService;

import java.io.IOException;
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
    @ApiOperation(value = "adds new asset", response = Asset.class)
    public ResponseEntity<?> addAsset(@RequestBody Asset newAsset) throws IOException {
        return ResponseEntity.ok(assetsService.addAsset(newAsset));
    }

    @GetMapping
    @ApiOperation(value = "returns all assets", response = Asset.class, responseContainer = "List")
    public ResponseEntity<?> getAllAssets() throws IOException {
        return ResponseEntity.ok(assetsService.getAllAssets());
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "returns asset with given id", response = Asset.class)
    public ResponseEntity<?> getAsset(@PathVariable String id) throws IOException {
        return ResponseEntity.ok(assetsService.getById(id));
    }

    @GetMapping(value = "/ids")
    @ApiOperation(value = "returns asset by given list of ids", response = Asset.class, responseContainer = "List")
    public ResponseEntity<?> getAssets(@RequestParam List<String> ids) throws IOException {
        return ResponseEntity.ok(assetsService.getByIds(ids));
    }

    @GetMapping(value = "/{categoryId}/{name}")
    @ApiOperation(
            value = "returns asset with given name within given category",
            response = Asset.class)
    public ResponseEntity<?> getAssetByCategoryAndName(@PathVariable String categoryId, @PathVariable String name) throws IOException {
        return assetsService.getByCategoryIdAndName(categoriesService.getMatchingCategoryIds(categoryId), name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/name/{name}")
    @ApiOperation(value = "returns assets with given name",
            response = Asset.class,
            responseContainer = "List")
    public ResponseEntity<?> getAssetsByName(@PathVariable String name) throws IOException {
        return ResponseEntity.ok(assetsService.getByName(name));
    }

    @GetMapping(value = "/category/{categoryId}")
    @ApiOperation(value = "returns all assets within given category",
            response = Asset.class,
            responseContainer = "List")
    public ResponseEntity<?> getAssetsByCategory(@PathVariable String categoryId) throws IOException {
        return ResponseEntity.ok(assetsService.getByCategoryId(categoryId));
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "deletes asset with given id")
    public ResponseEntity<?> deleteAsset(@PathVariable String id) throws IOException {
        if (assetsService.deleteAsset(id) == DocWriteResponse.Result.DELETED)
            return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @PutMapping
    @ApiOperation(
            value = "updates asset given in body",
            notes = "asset is recognized by id, name is mandatory, other attributes will be removed if not provided",
            response = Asset.class)
    public ResponseEntity<?> updateAsset(@RequestBody Asset asset) throws IOException {
        if (asset.getId() == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(assetsService.saveAsset(asset));
    }

    @PutMapping(value = "/attributes")
    @ApiOperation(value = "updates asset attributes only",
            notes = "asset is recognized by id given in body",
            response = Asset.class)
    public ResponseEntity<?> updateAssetAttributes(@RequestBody AssetAttributesUpdate attributesUpdate) throws IOException {
        if (attributesUpdate.getId() == null)
            return ResponseEntity.badRequest().build();
        Optional<Asset> asset = assetsService.updateAssetAttributes(attributesUpdate);
        if (asset.isPresent())
            return ResponseEntity.ok(assetsService.saveAsset(asset.get()));
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/filter")
    @ApiOperation(value = "Filters all assets",
            notes = "Filter all assets based on fields given in body. These fields are: id, name, categoryId, attributesMap")
    public ResponseEntity<?> filterAssetsByFields(@RequestBody AssetsFilter assetsFilter) throws IOException {
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

    @PutMapping(value = "/{id}/register/computer")
    @ApiOperation(value = "Saves identifier of computer connected to server in asset",
            response = Asset.class)
    public ResponseEntity<?> registerComputer(@PathVariable String id, @RequestBody String computerIdentifier) throws IOException {
        return assetsService.registerComputer(id, computerIdentifier)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}