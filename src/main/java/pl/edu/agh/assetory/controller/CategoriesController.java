package pl.edu.agh.assetory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.assetory.model.Category;
import pl.edu.agh.assetory.service.CategoriesService;


@RestController
@RequestMapping(value = "/categories")
public class CategoriesController {
    private CategoriesService categoriesService;

    @Autowired
    CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody Category newCategory) {
        return ResponseEntity.ok(categoriesService.addCategory(newCategory));
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoriesService.getAllCategories());
    }

    @PutMapping
    public ResponseEntity<?> updateCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoriesService.updateCategory(category));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getCategory(@PathVariable String id) {
        return ResponseEntity.ok(categoriesService.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable String id) {
        categoriesService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/subcategories")
    public ResponseEntity<?> getSubcategories(@PathVariable String id) {
        Category category = categoriesService.findById(id);
        return (category == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(categoriesService.getSubcategories(category));
    }

    @GetMapping(value = "/{id}/supercategories")
    public ResponseEntity<?> getSuperCategories(@PathVariable String id) {
        Category category = categoriesService.findById(id);
        return (category == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(categoriesService.getSuperCategories(category));
    }
}





