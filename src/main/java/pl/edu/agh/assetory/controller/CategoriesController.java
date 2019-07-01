package pl.edu.agh.assetory.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.assetory.model.Category;
import pl.edu.agh.assetory.model.CategoryTree;
import pl.edu.agh.assetory.service.CategoriesService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/categories")
public class CategoriesController {
    private CategoriesService categoriesService;

    @Autowired
    CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @PostMapping
    @ApiOperation(value = "adds new category",
            response = Category.class)
    public ResponseEntity<?> addCategory(@RequestBody Category newCategory) {
        return ResponseEntity.ok(categoriesService.addCategory(newCategory));
    }

    @GetMapping
    @ApiOperation(value = "returns all categories",
            response = Category.class,
            responseContainer = "List")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoriesService.getAllCategories());
    }

    @PutMapping
    @ApiOperation(value = "updates category given in body",
            notes = "category is recognized by id, categoryId name and attributes list can be updated")
    public ResponseEntity<?> updateCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoriesService.updateCategory(category));
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "returns category with given id",
            response = Category.class)
    public ResponseEntity<?> getCategory(@PathVariable String id) {
        return ResponseEntity.of(categoriesService.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "deletes category with given id",
            notes = "assets will be moved to first supercategory")
    public ResponseEntity<?> deleteCategoryWithoutAssets(@PathVariable String id) {
        return categoriesService.findById(id)
                .map(category -> {
                    categoriesService.deleteCategory(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}/assets")
    @ApiOperation(value = "deletes category by id with its all assets")
    public ResponseEntity<?> deleteCategoryWithAssets(@PathVariable String id) {
        return categoriesService.findById(id)
                .map(category -> {
                    categoriesService.deleteCategoryWithAssets(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/trees")
    public ResponseEntity<?> getCategoryTrees() {
        List<CategoryTree> categoryTrees = StreamSupport
                .stream(categoriesService.getRootCategories().spliterator(), false)
                .map(category -> categoriesService.createCategoryTree(category))
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryTrees);
    }

    @GetMapping(value = "/{id}/attributes")
    public ResponseEntity<?> getCategoryAttributes(@PathVariable String id) {
        return categoriesService.findById(id)
                .map(category -> ResponseEntity.ok(categoriesService.getCategoryAttributes(category)))
                .orElse(ResponseEntity.notFound().build());
    }
}





