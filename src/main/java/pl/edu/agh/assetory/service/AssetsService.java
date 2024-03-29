package pl.edu.agh.assetory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.elasticsearch.action.DocWriteResponse;
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
import pl.edu.agh.assetory.model.AssetsFilter;
import pl.edu.agh.assetory.model.attributes.AssetAttribute;
import pl.edu.agh.assetory.model.attributes.CategoryAttribute;
import pl.edu.agh.assetory.model.update.AssetAttributesUpdate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AssetsService {
    private RestHighLevelClient client;

    private ObjectMapper objectMapper;

    @Autowired
    public AssetsService(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }


    public void putMappings() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("asset");
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
                builder.startObject("categoryId");
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

    public Asset addAsset(Asset asset) throws IOException {
        IndexRequest request = new IndexRequest("asset");
        Map<String, Object> documentMapper = objectMapper.convertValue(asset, Map.class);
        request.source(documentMapper, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        String id = response.getId();
        UpdateRequest idUpdate = new UpdateRequest("asset", id).doc("id", id);
        asset.setId(id);
        addRelatedAssets(asset.getId(), asset.getRelatedAssetsIds());
        client.update(idUpdate, RequestOptions.DEFAULT);
        return asset;
    }

    public List<Asset> getAllAssets() throws IOException {
        SearchRequest searchRequest = new SearchRequest("asset");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(10000);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }


    public Optional<Asset> getById(String assetId) throws IOException {
        GetRequest getRequest = new GetRequest("asset", assetId);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        if (getResponse.isExists()) {
            Map<String, Object> resultMap = getResponse.getSource();
            return Optional.of(objectMapper.convertValue(resultMap, Asset.class));
        } else {
            return Optional.empty();
        }
    }

    public Iterable<Asset> getByIds(Collection<String> assetIds) throws IOException {
        SearchRequest searchRequest = new SearchRequest("asset");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.idsQuery().addIds(assetIds.toArray(new String[assetIds.size()]))).size(10000);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    public Optional<Asset> getByCategoryIdAndName(Collection<String> categoryIds, String name) throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery(AssetsFilter.CATEGORY_ID_FIELD, categoryIds.toArray()))
                .must(QueryBuilders.termQuery(AssetsFilter.NAME_FIELD, name));
        SearchRequest searchRequest = new SearchRequest("asset").source(new SearchSourceBuilder().query(queryBuilder).size(10000));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse).stream().findFirst();
    }

    public List<Asset> getByName(String name) throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery(AssetsFilter.NAME_FIELD, name));
        SearchRequest searchRequest = new SearchRequest("asset").source(new SearchSourceBuilder().query(queryBuilder).size(10000));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    public List<Asset> getByCategoryId(String categoryId) throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery(AssetsFilter.CATEGORY_ID_FIELD, categoryId));
        SearchRequest searchRequest = new SearchRequest("asset").source(new SearchSourceBuilder().query(queryBuilder).size(10000));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    public Optional<Asset> updateAssetAttributes(AssetAttributesUpdate attributesUpdate) throws IOException {
        Optional<Asset> assetOpt = getById(attributesUpdate.getId());
        if (assetOpt.isPresent()) {
            Asset asset = assetOpt.get();
            List<CategoryAttribute> assetAttributes = asset.getAttributes().stream()
                    .map(AssetAttribute::getAttribute)
                    .collect(Collectors.toList());
            Map<String, String> attributes = attributesUpdate.getAttributes();
            attributes.forEach((name, value) -> asset.getAttribute(name).ifPresent(attribute -> {
                int index = findAttributeIndex(assetAttributes, name);
                asset.removeAttribute(name);
                asset.addAttribute(index, new AssetAttribute(attribute.getAttribute(), value));
            }));
            return Optional.of(asset);
        }
        return Optional.empty();
    }

    public Optional<Asset> addRelatedAssets(String id, Set<String> relatedAssetsIds) throws IOException {
        Optional<Asset> assetOpt = getById(id);
        if (assetOpt.isPresent()) {
            Asset asset = assetOpt.get();
            asset.addRelatedAssetIds(relatedAssetsIds);
            relatedAssetsIds.forEach(relatedAssetId -> {
                try {
                    Optional<Asset> relatedAssetOpt = getById(relatedAssetId);
                    if (relatedAssetOpt.isPresent()) {
                        Asset relatedAsset = relatedAssetOpt.get();
                        relatedAsset.addRelatedAssetIds(Sets.newHashSet(id));
                        saveAsset(relatedAsset);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return Optional.of(asset);
        }
        return Optional.empty();
    }

    public Optional<Asset> deleteRelatedAssets(String id, List<String> relatedAssetsIds) throws IOException {
        Optional<Asset> assetOpt = getById(id);
        if (assetOpt.isPresent()) {
            Asset asset = assetOpt.get();
            asset.removeRelatedAssetIds(relatedAssetsIds);
            relatedAssetsIds.forEach(relatedAssetId -> {
                try {
                    Optional<Asset> relatedAssetOpt = getById(relatedAssetId);
                    if (relatedAssetOpt.isPresent()) {
                        Asset relatedAsset = relatedAssetOpt.get();
                        relatedAsset.removeRelatedAssetIds(Collections.singletonList(id));
                        saveAsset(relatedAsset);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return Optional.of(asset);
        }
        return Optional.empty();
    }

    private int findAttributeIndex(Collection<CategoryAttribute> attributes, String attributeName) {
        return attributes.stream()
                .map(CategoryAttribute::getName)
                .collect(Collectors.toList())
                .indexOf(attributeName);
    }

    public DocWriteResponse.Result deleteAsset(String assetId) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("asset", assetId);
        Optional<Asset> assetOpt = getById(assetId);
        if (assetOpt.isPresent()) {
            Asset asset = assetOpt.get();
            List<Asset> relatedAssets = Lists.newArrayList(getByIds(asset.getRelatedAssetsIds()));
            for (Asset relatedAsset : relatedAssets) {
                relatedAsset.removeRelatedAssetIds(Collections.singletonList(assetId));
                saveAsset(relatedAsset);
            }
        }
        return client.delete(deleteRequest, RequestOptions.DEFAULT).getResult();
    }

    List<Asset> getSearchResult(SearchResponse response) {

        SearchHit[] searchHit = response.getHits().getHits();

        List<Asset> assets = new ArrayList<>();

        if (searchHit.length > 0) {

            Arrays.stream(searchHit)
                    .forEach(hit -> assets
                            .add(objectMapper
                                    .convertValue(hit.getSourceAsMap(),
                                            Asset.class))
                    );
        }

        return assets;
    }


    public Asset saveAsset(Asset asset) throws IOException {
        Map<String, Object> documentMapper = objectMapper.convertValue(asset, Map.class);
        UpdateRequest update = new UpdateRequest("asset", asset.getId()).doc(documentMapper);
        client.update(update, RequestOptions.DEFAULT);
        return asset;
    }

    void saveAssets(Collection<Asset> assets) throws IOException {
        if (!assets.isEmpty()) {
            BulkRequest bulkRequest = new BulkRequest();
            assets.forEach(asset -> {
                UpdateRequest updateRequest = new UpdateRequest("asset", asset.getId()).
                        doc(objectMapper.convertValue(asset, Map.class));
                bulkRequest.add(updateRequest);
            });
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
        }
    }

    void deleteAssets(Collection<Asset> assets) throws IOException {
        if (!assets.isEmpty()) {
            BulkRequest bulkRequest = new BulkRequest();
            for (Asset asset : assets) {
                List<Asset> relatedAssets = Lists.newArrayList(getByIds(asset.getRelatedAssetsIds()));
                for (Asset relatedAsset : relatedAssets) {
                    relatedAsset.removeRelatedAssetIds(Collections.singletonList(asset.getId()));
                    saveAsset(relatedAsset);
                }
                DeleteRequest deleteRequest = new DeleteRequest("asset", asset.getId());
                bulkRequest.add(deleteRequest);
            }
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
        }
    }


    public Iterable<Asset> filterAssetsByFields(AssetsFilter assetsFilter) throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        BoolQueryBuilder filtersQuery = QueryBuilders.boolQuery();
        for (Map.Entry<String, List<String>> filter : assetsFilter.getFilters().entrySet()) {
            String attributeName = filter.getKey();
            BoolQueryBuilder fieldFilter = QueryBuilders.boolQuery();
            for (String value : filter.getValue()) {
                BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
                filterQuery.must(QueryBuilders.termQuery("attributes.attribute.name.keyword", attributeName));
                filterQuery.must(QueryBuilders.termQuery("attributes.value.keyword", value));
                fieldFilter.should(filterQuery);
            }
            filtersQuery.must(fieldFilter);
        }
        if (filtersQuery.hasClauses()) queryBuilder.must(filtersQuery);

        Optional.ofNullable(assetsFilter.getName())
                .map(nameList -> queryBuilder.must(getQueryForField(AssetsFilter.NAME_FIELD, nameList)));
        Optional.ofNullable(assetsFilter.getId())
                .map(idList -> queryBuilder.must(getQueryForField(AssetsFilter.ID_FIELD, idList)));
        Optional.ofNullable(assetsFilter.getCategoryId())
                .map(categoryIdList -> queryBuilder.must(getQueryForField(AssetsFilter.CATEGORY_ID_FIELD, categoryIdList)));

        SearchRequest searchRequest = new SearchRequest("asset");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    private BoolQueryBuilder getQueryForField(String fieldName, Collection<?> filterValues) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (Object value : filterValues) {
            queryBuilder.should(QueryBuilders.matchQuery(fieldName, value).operator(Operator.AND));
        }
        return queryBuilder;
    }

    public Optional<Asset> registerComputer(String assetId, String computerIdentifier) throws IOException {
        Optional<Asset> assetOpt = getById(assetId);
        if (assetOpt.isPresent()) {
            Asset asset = assetOpt.get();
            asset.setConnectedComputerId(computerIdentifier);
            saveAsset(asset);
            return Optional.of(asset);
        }
        return Optional.empty();
    }

    public void disconnectComputer(Asset asset) throws IOException {
        asset.setConnectedComputerId(null);
        saveAsset(asset);
    }
}