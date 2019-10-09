package pl.edu.agh.assetory.service;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.model.Category;
import pl.edu.agh.assetory.model.CategoryTree;
import pl.edu.agh.assetory.model.DBEntity;
import pl.edu.agh.assetory.model.attributes.CategoryAttribute;
import pl.edu.agh.assetory.repository.AssetsRepository;
import pl.edu.agh.assetory.repository.CategoriesRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CategoriesService {
    @Autowired
    @Qualifier("categoriesRepository")
    private CategoriesRepository categoriesRepository;

    @Autowired
    @Qualifier("assetsRepository")
    private AssetsRepository assetsRepository;

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

    public void deleteCategory(Category category) {
        updateParentCategorySubcategoryIds(category);
        String deletedCategoryParentId = category.getParentCategoryId();
        List<Category> childCategories = categoriesRepository.getCategoriesByParentCategoryId(category.getId());
        if (!childCategories.isEmpty()) {
            childCategories.forEach(c -> c.setParentCategoryId(deletedCategoryParentId));
            categoriesRepository.saveAll(childCategories);
        }
        List<Asset> assets = assetsRepository.getAssetsByCategoryId(category.getId());
        if (!assets.isEmpty()) {
            assets.forEach(a -> a.setCategoryId(deletedCategoryParentId));
            assetsRepository.saveAll(assets);
        }
        categoriesRepository.delete(category);
    }

    public Iterable<Category> getAllCategories() {
        return categoriesRepository.findAll();
    }

    public void deleteCategoryWithContent(Category category) {
        List<Asset> assets = assetsRepository.getAssetsByCategoryId(category.getId());
        assetsRepository.deleteAll(assets);
        List<Category> childCategories = categoriesRepository.getCategoriesByParentCategoryId(category.getId());
        childCategories.forEach(this::deleteCategoryWithContent);
        removeFromParentCategorySubcategoryIds(category);
        categoriesRepository.delete(category);
    }

    public Iterable<Category> getRootCategories() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(Category.PARENT_ID_FIELD_KEY));
        return categoriesRepository.search(queryBuilder);
    }

    public CategoryTree createCategoryTree(Category category) {
        List<CategoryTree> subcategories = category.getSubcategoryIds().stream()
                .map(this::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::createCategoryTree)
                .collect(Collectors.toList());
        return new CategoryTree(category, subcategories);
    }

    public List<CategoryAttribute> getCategoryAttributes(Category category) {
        if (category.getParentCategoryId() != null && findById(category.getParentCategoryId()).isPresent()) {
            return Stream
                    .concat(getCategoryAttributes(findById(category.getParentCategoryId()).get()).stream(), category.getAdditionalAttributes().stream())
                    .collect(Collectors.toList());
        } else {
            return category.getAdditionalAttributes();
        }
    }

    public Set<String> getMatchingCategoryIds(String categoryId) {
        Optional<Category> foundCategory = findById(categoryId);
        if (foundCategory.isPresent()) {
            Category category = foundCategory.get();
            Set<String> idsSet = Sets.newHashSet((category.getSubcategoryIds()));
            idsSet.add(category.getId());
            category.getSubcategoryIds().stream()
                    .map(this::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(DBEntity::getId)
                    .map(this::getMatchingCategoryIds)
                    .forEach(idsSet::addAll);
            return idsSet;
        }
        return Sets.newHashSet();
    }

    public Map<String, Set<String>> getCategoryAttributesValues(Category category) {
        List<Asset> assets = getAssetsInCategory(category.getId());
        List<String> attributeNames = getCategoryAttributes(category).stream()
                .map(attribute -> attribute.getName())
                .collect(Collectors.toList());
        Map<String, Set<String>> attributesValues = Maps.newHashMap();
        attributeNames.forEach(attribute -> attributesValues.put(attribute, assets.stream()
                .map(asset -> asset.getAttributes().stream()
                        .filter(attr -> attr.getAttribute().getName().equals(attribute))
                        .findFirst())
                .filter(assetAttribute -> assetAttribute.isPresent())
                .map(assetAttribute -> assetAttribute.get().getValue())
                .collect(Collectors.toSet())));
        return attributesValues;
    }

    private List<Asset> getAssetsInCategory(String categoryId) {
        return assetsRepository.getAssetsByCategoryId(categoryId);
    }

    private void updateParentCategorySubcategoryIds(Category category) {
        Optional.ofNullable(category.getParentCategoryId()).ifPresent(id -> {
            Optional<Category> parentCategory = findById(id);
            parentCategory.ifPresent(parent -> {
                parent.getSubcategoryIds().remove(category.getId());
                parent.getSubcategoryIds().addAll(category.getSubcategoryIds());
                categoriesRepository.save(parent);
            });
        });
    }

    private void removeFromParentCategorySubcategoryIds(Category category) {
        Optional.ofNullable(category.getParentCategoryId()).ifPresent(id -> {
            Optional<Category> parentCategory = findById(id);
            parentCategory.ifPresent(parent -> {
                parent.getSubcategoryIds().remove(category.getId());
                categoriesRepository.save(parent);
            });
        });
    }
}
