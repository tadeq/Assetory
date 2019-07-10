package pl.edu.agh.assetory.service;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.model.AssetsFilter;
import pl.edu.agh.assetory.repository.AssetsRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class AssetsService {

    @Autowired
    @Qualifier("assetsRepository")
    private AssetsRepository assetsRepository;

    public Optional<Asset> getById(String assetId) {
        return assetsRepository.findById(assetId);
    }

    public List<Asset> getByName(String name) {
        return assetsRepository.getAssetsByName(name);
    }

    public Asset addAsset(Asset asset) {
        return assetsRepository.save(asset);
    }

    public Asset updateAsset(Asset asset) {
        return assetsRepository.save(asset);
    }

    public void deleteAsset(String assetId) {
        assetsRepository.deleteById(assetId);
    }

    public Iterable<Asset> getAllAssets() {
        return assetsRepository.findAll();
    }

    public Iterable<Asset> filterAssetsByFields(AssetsFilter assetsFilter) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (Map.Entry<String, List<String>> filter : assetsFilter.getFilters().entrySet()) {
            queryBuilder.must(getQueryForField(filter.getKey(), filter.getValue()));
        }
        Optional<List<String>> nameFilter = Optional.ofNullable(assetsFilter.getName());
        Optional<List<String>> categoryIdFilter = Optional.ofNullable(assetsFilter.getCategoryId());
        Optional<String> mainCategoryIdFilter = Optional.ofNullable(assetsFilter.getMainCategoryId());
        nameFilter.ifPresent(name -> queryBuilder.should(QueryBuilders.matchQuery(AssetsFilter.NAME_FIELD, name)));
        categoryIdFilter.ifPresent(categoryId -> queryBuilder.should(QueryBuilders.matchQuery(AssetsFilter.CATEGORY_ID_FIELD, categoryId)));
        mainCategoryIdFilter.ifPresent(mainCategoryId -> queryBuilder.should(QueryBuilders.matchQuery(AssetsFilter.CATEGORY_ID_FIELD, mainCategoryId)));

        return assetsRepository.search(queryBuilder);
    }

    private BoolQueryBuilder getQueryForField(String fieldName, Collection<?> filterValues) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (Object value : filterValues) {
            queryBuilder.should(QueryBuilders.matchQuery(fieldName, value));
        }
        return queryBuilder;
    }
}