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

        BoolQueryBuilder filtersQuery = QueryBuilders.boolQuery();
        for (Map.Entry<String, List<String>> filter : assetsFilter.getFilters().entrySet()) {
            String attributeName = filter.getKey();
            BoolQueryBuilder fieldFilter = QueryBuilders.boolQuery();
            for (String value : filter.getValue()) {
                BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
                filterQuery.must(QueryBuilders.matchQuery("attributes.attribute.name", attributeName));
                filterQuery.must(QueryBuilders.matchQuery("attributes.value", value));
                fieldFilter.should(filterQuery);
            }
            filtersQuery.must(fieldFilter);
        }
        if (filtersQuery.hasClauses()) queryBuilder.must(filtersQuery);

        Optional.ofNullable(assetsFilter.getName())
                .map(nameList -> queryBuilder.must(getQueryForField(AssetsFilter.NAME_FIELD, nameList)));
        Optional.ofNullable(assetsFilter.getCategoryId())
                .map(categoryIdList -> queryBuilder.must(getQueryForField(AssetsFilter.CATEGORY_ID_FIELD, categoryIdList)));
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