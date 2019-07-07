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
            notes = "adds category that doesn't have a parent category",
            response = Category.class)
    public ResponseEntity<?> addCategory(@RequestBody Category newCategory) {
        return ResponseEntity.ok(categoriesService.addCategory(newCategory));
    }

    @PostMapping(value = "/{id}")
    @ApiOperation(value = "adds new subcategory",
            notes = "adds category that will be a subcategory of the category with given id")
    public ResponseEntity<?> addSubcategory(@PathVariable String id, @RequestBody Category subcategory) {
        return categoriesService.findById(id)
                .map(category -> {
                    subcategory.setParentCategoryId(category.getId());
                    Category savedSubcategory = categoriesService.addCategory(subcategory);
                    category.addSubcategory(savedSubcategory.getId());
                    categoriesService.addCategory(category);
                    return ResponseEntity.ok(savedSubcategory);
                })
                .orElse(ResponseEntity.notFound().build());
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
            notes = "category is recognized by id, categoryId name and attributeNames list can be updated")
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
            notes = "assets will be moved to first super category")
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
    @ApiOperation(value = "returns categories hierarchy")
    public ResponseEntity<?> getCategoryTrees() {
        List<CategoryTree> categoryTrees = StreamSupport
                .stream(categoriesService.getRootCategories().spliterator(), false)
                .map(category -> categoriesService.createCategoryTree(category))
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryTrees);
    }

    @GetMapping(value = "/{id}/attributes")
    @ApiOperation(value = "returns attributeNames required for asset in category",
            notes = "attributeNames of all super categories are also included")
    public ResponseEntity<?> getCategoryAttributes(@PathVariable String id) {
        return categoriesService.findById(id)
                .map(category -> ResponseEntity.ok(categoriesService.getCategoryAttributes(category)))
                .orElse(ResponseEntity.notFound().build());
    }
}





