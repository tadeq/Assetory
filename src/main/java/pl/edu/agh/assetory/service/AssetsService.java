package pl.edu.agh.assetory.service;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.model.AssetsFilter;
import pl.edu.agh.assetory.repository.AssetsRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


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
        if (assetsFilter.getCategoryId() != null) {
            queryBuilder = queryBuilder.must(getQueryForField(Asset.CATEGORY_ID_FIELD_KEY, assetsFilter.getCategoryId()));
        }
        if (assetsFilter.getName() != null) {
            queryBuilder = queryBuilder.must(getQueryForField(Asset.NAME_FIELD_KEY, assetsFilter.getName()));
        }
        if (assetsFilter.getAttributesMap() != null) {
            for (Map.Entry<String, List<String>> entry : assetsFilter.getAttributesMap().entrySet()) {
                queryBuilder = queryBuilder.must(getQueryForField(Asset.ATTRIBUTES_MAP_FIELD_KEY + "." + entry.getKey(), entry.getValue()));
            }
        }
        if (assetsFilter.getLocalisation() != null) {
            queryBuilder = queryBuilder.must(getQueryForField(Asset.LOCALISATION_FIELD_KEY, assetsFilter.getLocalisation()));
        }
        if (assetsFilter.getBackup() != null) {
            queryBuilder = queryBuilder.must(getQueryForField(Asset.BACKUP_FIELD_KEY, assetsFilter.getBackup()));
        }
        if (assetsFilter.getLicense() != null) {
            queryBuilder = queryBuilder.must(getQueryForField(Asset.LICENSE_FIELD_KEY, assetsFilter.getLicense()));
        }
        if (assetsFilter.getValue() != null) {
            List<String> valueList = assetsFilter.getValue().stream().map(BigDecimal::toPlainString).collect(Collectors.toList());
            queryBuilder = queryBuilder.must(getQueryForField(Asset.VALUE_FIELD_KEY, valueList));
        }
        if (assetsFilter.getUser() != null) {
            queryBuilder = queryBuilder.must(getQueryForField(Asset.USER_FIELD_KEY, assetsFilter.getUser()));
        }
        if (assetsFilter.getOwner() != null) {
            queryBuilder = queryBuilder.must(getQueryForField(Asset.OWNER_FIELD_KEY, assetsFilter.getOwner()));
        }
        return assetsRepository.search(queryBuilder);
    }

    private BoolQueryBuilder getQueryForField(String fieldName, List<String> filterValues) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (String value : filterValues) {
            query = query.should(QueryBuilders.matchQuery(fieldName, value));
        }
        return query;
    }
}