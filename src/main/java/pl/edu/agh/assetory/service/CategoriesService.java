package pl.edu.agh.assetory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.agh.assetory.Repository.CategoriesRepository;
import pl.edu.agh.assetory.model.Category;

import java.util.List;

@Service
public class CategoriesService {
    @Autowired
    @Qualifier("categoriesRepository")
    private CategoriesRepository categoriesRepository;

    public List<Category> findByName(String categoryName) {
        return categoriesRepository.findCategoriesByName(categoryName);
    }

    public Category addCategory(Category newCategory) {
        return categoriesRepository.save(newCategory);
    }

    public Iterable<Category> getAllCategories() {
        return categoriesRepository.findAll();
    }

}
