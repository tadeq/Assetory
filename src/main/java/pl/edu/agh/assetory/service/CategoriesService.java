package pl.edu.agh.assetory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.model.Category;
import pl.edu.agh.assetory.model.CategoryTree;
import pl.edu.agh.assetory.model.DBEntity;
import pl.edu.agh.assetory.model.attributes.CategoryAttribute;
import pl.edu.agh.assetory.utils.NumberAwareStringComparator;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CategoriesService {
    private RestHighLevelClient client;

    private ObjectMapper objectMapper;
    private AssetsService assetsService;

    @Autowired
    public CategoriesService(RestHighLevelClient client, ObjectMapper objectMapper, AssetsService assetsService) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.assetsService = assetsService;
    }


    public void putMappings() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("category");
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("id");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("name");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("parentCategoryId");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping(builder);
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    public Optional<Category> findById(String categoryId) {
        GetRequest getRequest = new GetRequest("category", categoryId);
        GetResponse getResponse = null;
        try {
            getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (getResponse.isExists()) {
            Map<String, Object> resultMap = getResponse.getSource();
            return Optional.of(objectMapper.convertValue(resultMap, Category.class));
        } else {
            return Optional.empty();
        }
    }

    public List<Category> findByName(String categoryName) throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery(Category.NAME_FIELD_KEY, categoryName));
        SearchRequest searchRequest = new SearchRequest("category").source(new SearchSourceBuilder().query(queryBuilder).size(10000));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    public Category addCategory(Category newCategory) throws IOException {
        IndexRequest request = new IndexRequest("category");
        Map<String, Object> documentMapper = objectMapper.convertValue(newCategory, Map.class);
        request.source(documentMapper, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        String id = response.getId();
        UpdateRequest idUpdate = new UpdateRequest("category", id).doc("id", id);
        newCategory.setId(id);
        client.update(idUpdate, RequestOptions.DEFAULT);
        return newCategory;
    }

    public void deleteCategory(Category category) throws IOException {
        updateParentCategorySubcategoryIds(category);
        String deletedCategoryParentId = category.getParentCategoryId();
        List<Category> childCategories = getCategoriesByParentCategoryId(category.getId());
        if (!childCategories.isEmpty()) {
            childCategories.forEach(c -> c.setParentCategoryId(deletedCategoryParentId));
            saveCategories(childCategories);
        }
        List<Asset> assets = assetsService.getByCategoryId(category.getId());
        if (!assets.isEmpty()) {
            assets.forEach(a -> a.setCategoryId(deletedCategoryParentId));
            assetsService.saveAssets(assets);
        }
        deleteById(category.getId());
    }

    public Iterable<Category> getAllCategories() throws IOException {
        SearchRequest searchRequest = new SearchRequest("category");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(10000);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    public void deleteCategoryWithContent(Category category) throws IOException {
        List<Asset> assets = assetsService.getByCategoryId(category.getId());
        assetsService.deleteAssets(assets);
        List<Category> childCategories = getCategoriesByParentCategoryId(category.getId());
        for (Category childCategory : childCategories) {
            deleteCategoryWithContent(childCategory);
        }
        removeFromParentCategorySubcategoryIds(category);
        deleteCategory(category);
    }

    public Iterable<Category> getRootCategories() throws IOException {

        SearchRequest searchRequest = new SearchRequest("category");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(Category.PARENT_ID_FIELD_KEY));
        searchSourceBuilder.query(queryBuilder).size(10000);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
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

    public Map<String, List<String>> getCategoryAttributesValues(Category category, boolean withSubcategories) throws IOException {
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

    private List<Asset> getAssetsInCategory(String categoryId, boolean withSubcategories) throws IOException {
        if (withSubcategories) {
            Set<String> matchingCategoriesId = getMatchingCategoryIds(categoryId);
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            for (Object value : matchingCategoriesId) {
                queryBuilder.should(QueryBuilders.matchQuery(Asset.CATEGORY_ID_FIELD_KEY, value).operator(Operator.AND));
            }
            SearchRequest searchRequest = new SearchRequest("category");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(queryBuilder).size(10000);
            searchRequest.source(searchSourceBuilder);
            return assetsService.getSearchResult(client.search(searchRequest, RequestOptions.DEFAULT));
        }
        return assetsService.getByCategoryId(categoryId);
    }

    private void updateParentCategorySubcategoryIds(Category category) {
        Optional.ofNullable(category.getParentCategoryId()).ifPresent(id -> {
            Optional<Category> parentCategory = findById(id);
            parentCategory.ifPresent(parent -> {
                parent.getSubcategoryIds().remove(category.getId());
                parent.getSubcategoryIds().addAll(category.getSubcategoryIds());
                try {
                    updateCategory(parent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void removeFromParentCategorySubcategoryIds(Category category) {
        Optional.ofNullable(category.getParentCategoryId()).ifPresent(id -> {
            Optional<Category> parentCategory = findById(id);
            parentCategory.ifPresent(parent -> {
                parent.getSubcategoryIds().remove(category.getId());
                try {
                    updateCategory(parent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private List<Category> getCategoriesByParentCategoryId(String parentCategoryId) throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery(Category.PARENT_ID_FIELD_KEY, parentCategoryId));
        SearchRequest searchRequest = new SearchRequest("category").source(new SearchSourceBuilder().query(queryBuilder).size(10000));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    private List<Category> getSearchResult(SearchResponse response) {

        SearchHit[] searchHit = response.getHits().getHits();

        List<Category> categories = new ArrayList<>();

        if (searchHit.length > 0) {

            Arrays.stream(searchHit)
                    .forEach(hit -> categories
                            .add(objectMapper
                                    .convertValue(hit.getSourceAsMap(),
                                            Category.class))
                    );
        }

        return categories;
    }

    public Category updateCategory(Category category) throws IOException {
        Map<String, Object> documentMapper = objectMapper.convertValue(category, Map.class);
        UpdateRequest update = new UpdateRequest("category", category.getId()).doc(documentMapper);
        client.update(update, RequestOptions.DEFAULT);
        return category;
    }

    private void saveCategories(Collection<Category> categories) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        categories.forEach(category -> {
            UpdateRequest updateRequest = new UpdateRequest("category", category.getId()).
                    doc(objectMapper.convertValue(category, Map.class));
            bulkRequest.add(updateRequest);
        });
        client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    private void deleteById(String categoryId) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("category", categoryId);
        client.delete(deleteRequest, RequestOptions.DEFAULT).getResult();
    }
}
