package pl.edu.agh.assetory.service;

import com.google.common.collect.Iterables;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.edu.agh.assetory.repository.CategoriesRepository;
import pl.edu.agh.assetory.model.Category;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriesService {
    @Autowired
    @Qualifier("categoriesRepository")
    private CategoriesRepository categoriesRepository;

    public Category findById(String categoryId){
        return categoriesRepository.findCategoryById(categoryId);
    }

    public List<Category> findByName(String categoryName) {
        return categoriesRepository.findCategoriesByName(categoryName);
    }

    public Category addCategory(Category newCategory) {
        return categoriesRepository.save(newCategory);
    }

    public Category updateCategory(Category category){
        return categoriesRepository.save(category);
    }

    public void deleteCategory(String categoryId){

    }

    public Iterable<Category> getAllCategories() {
        return categoriesRepository.findAll();
    }

    public Iterable<Category> findSuperCategories(Category category) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        List<String> categoriesNames = category.getPath().stream()
                .map(StringUtils::capitalize)
                .collect(Collectors.toList());
        for (String categoryName : categoriesNames) {
            queryBuilder = queryBuilder.should(QueryBuilders.matchQuery("name", categoryName));
        }
        return categoriesRepository.search(queryBuilder);
    }

    public Iterable<Category> getSubcategories(String categoryId){
        return Iterables.cycle(); //TODO implement method
    }

}
