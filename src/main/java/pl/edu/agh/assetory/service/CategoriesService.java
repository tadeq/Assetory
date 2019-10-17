package pl.edu.agh.assetory.service;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
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
import pl.edu.agh.assetory.utils.NumberAwareStringComparator;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    public Map<String, List<String>> getCategoryAttributesValues(Category category, boolean withSubcategories) {
        List<Asset> assets = getAssetsInCategory(category.getId(), withSubcategories);
        List<String> attributeNames = getCategoryAttributes(category).stream()
                .map(CategoryAttribute::getName)
                .collect(Collectors.toCollection(LinkedList::new));
        Map<String, List<String>> attributesValues = Maps.newLinkedHashMap();
        attributesValues.put(Asset.NAME_FIELD_KEY, assets.stream()
                .map(Asset::getName)
                .distinct()
                .collect(Collectors.toList()));
        attributeNames.forEach(attribute -> attributesValues.put(attribute, assets.stream()
                .map(asset -> asset.getAttributes().stream()
                        .filter(attr -> attr.getAttribute().getName().equals(attribute))
                        .findFirst())
                .filter(Optional::isPresent)
                .map(assetAttribute -> assetAttribute.get().getValue())
                .distinct()
                .collect(Collectors.toList())));
        attributesValues.forEach((name, values) -> values.sort(NumberAwareStringComparator.INSTANCE));
        return attributesValues;
    }

    private List<Asset> getAssetsInCategory(String categoryId, boolean withSubcategories) {
        if (withSubcategories) {
            Set<String> matchingCategoriesId = getMatchingCategoryIds(categoryId);
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            for (Object value : matchingCategoriesId) {
                queryBuilder.should(QueryBuilders.matchQuery(Asset.CATEGORY_ID_FIELD_KEY, value).operator(Operator.AND));
            }
            return StreamSupport.stream(assetsRepository.search(queryBuilder).spliterator(), false).collect(Collectors.toList());
        }
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
