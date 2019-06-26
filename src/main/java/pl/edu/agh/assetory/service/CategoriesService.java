package pl.edu.agh.assetory.service;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.edu.agh.assetory.model.Category;
import pl.edu.agh.assetory.repository.CategoriesRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriesService {
    @Autowired
    @Qualifier("categoriesRepository")
    private CategoriesRepository categoriesRepository;

    public Category findById(String categoryId) {
        return categoriesRepository.getCategoryById(categoryId);
    }

    public List<Category> findByName(String categoryName) {
        return categoriesRepository.getCategoriesByName(categoryName);
    }

    public Category addCategory(Category newCategory) {
        return categoriesRepository.save(newCategory);
    }

    public Category updateCategory(Category category) {
        return categoriesRepository.save(category);
    }

    public void deleteCategory(String categoryId) {
        categoriesRepository.deleteById(categoryId);
    }

    public Iterable<Category> getAllCategories() {
        return categoriesRepository.findAll();
    }

    /**
     * @param category
     * @return Collection of all super categories of a given category and this category as well.
     */
    public Iterable<Category> getSuperCategories(Category category) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        List<String> categoriesNames = Arrays.stream(category.getPath().split(Category.PATH_SEPARATOR))
                .map(StringUtils::capitalize)
                .collect(Collectors.toList());
        for (String categoryName : categoriesNames) {
            queryBuilder = queryBuilder.should(QueryBuilders.matchQuery(Category.NAME_FIELD_KEY, categoryName));
        }
        return categoriesRepository.search(queryBuilder);
    }

    /**
     * @param category
     * @return Collection of all subcategories of a given category without this category.
     */
    public Iterable<Category> getSubcategories(Category category) {
        String regex = category.getPath() + Category.PATH_SEPARATOR;
        PrefixQueryBuilder queryBuilder = QueryBuilders
                .prefixQuery(Category.PATH_FIELD_KEY, regex);
        return categoriesRepository.search(queryBuilder);
    }

}
