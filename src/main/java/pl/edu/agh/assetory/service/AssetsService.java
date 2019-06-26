package pl.edu.agh.assetory.service;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.repository.AssetsRepository;

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

    public Iterable<Asset> filterAssetsByFields(Asset assetTemplate) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (assetTemplate.getId() != null) {
            queryBuilder = queryBuilder.must(QueryBuilders.idsQuery().addIds(assetTemplate.getId()).types("asset"));
        }
        if (assetTemplate.getName() != null) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(Asset.NAME_FIELD_KEY, assetTemplate.getName()));
        }
        if (assetTemplate.getCategory() != null) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(Asset.CATEGORY_FIELD_KEY, assetTemplate.getCategory()));
        }
        if (assetTemplate.getAttributesMap() != null) {
            for (Map.Entry<String, String> entry : assetTemplate.getAttributesMap().entrySet()) {
                queryBuilder = queryBuilder
                        .must(QueryBuilders
                                .matchQuery(Asset.ATTRIBUTES_MAP_FIELD_KEY + "." + entry.getKey(), entry.getValue()));
            }
        }
        if (assetTemplate.getLocalisation() != null) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(Asset.LOCALISATION_FIELD_KEY, assetTemplate.getLocalisation()));
        }
        if (assetTemplate.getBackup() != null) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(Asset.BACKUP_FIELD_KEY, assetTemplate.getBackup()));
        }
        if (assetTemplate.getLicense() != null) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(Asset.LICENSE_FIELD_KEY, assetTemplate.getLicense()));
        }
        if (assetTemplate.getValue() != null) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(Asset.VALUE_FIELD_KEY, assetTemplate.getValue().toPlainString()));
        }
        if (assetTemplate.getUser() != null) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(Asset.USER_FIELD_KEY, assetTemplate.getUser()));
        }
        if (assetTemplate.getOwner() != null) {
            queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(Asset.OWNER_FIELD_KEY, assetTemplate.getOwner()));
        }
        return assetsRepository.search(queryBuilder);
    }
}