package pl.edu.agh.assetory.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(value = "/add")
    public String addCategory(@RequestBody Category newCategory) {
        categoriesService.addCategory(newCategory);
        return "Category saved in the db.";
    }

    @GetMapping(value = "/")
    public Iterable<Category> getAllCategories() {
        return categoriesService.getAllCategories();
    }

}
