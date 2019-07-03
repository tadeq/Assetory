package pl.edu.agh.assetory.service;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.agh.assetory.model.Category;
import pl.edu.agh.assetory.model.CategoryTree;
import pl.edu.agh.assetory.repository.CategoriesRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CategoriesService {
    @Autowired
    @Qualifier("categoriesRepository")
    private CategoriesRepository categoriesRepository;

    public Optional<Category> findById(String categoryId) {
        return categoriesRepository.findById(categoryId);
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

    public void deleteCategoryWithAssets(String categoryId) {

    }

    public Iterable<Category> getRootCategories() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(Category.PARENT_ID_FIELD_KEY));
        return categoriesRepository.search(queryBuilder);
    }

    public CategoryTree createCategoryTree(Category category) {
        List<CategoryTree> subcategories = category.getSubcategoriesIds().stream()
                .map(this::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::createCategoryTree)
                .collect(Collectors.toList());
        return new CategoryTree(category, subcategories);
    }

    public List<String> getCategoryAttributes(Category category) {
        if (category.getParentId() != null && findById(category.getParentId()).isPresent()) {
            return Stream
                    .concat(category.getAttributes().stream(), getCategoryAttributes(findById(category.getParentId()).get()).stream())
                    .collect(Collectors.toList());
        } else {
            return category.getAttributes();
        }
    }

    public Set<String> getMatchingCategoryIds(Category category) {
        Set<String> idsSet = new HashSet<>(category.getSubcategoriesIds());
        idsSet.add(category.getId());
        category.getSubcategoriesIds().stream()
                .map(this::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::getMatchingCategoryIds)
                .forEach(idsSet::addAll);
        return idsSet;

    }


}
