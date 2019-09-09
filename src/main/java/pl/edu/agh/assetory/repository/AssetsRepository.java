package pl.edu.agh.assetory.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.assetory.model.Asset;

import java.util.Collection;
import java.util.List;

@Repository
public interface AssetsRepository extends ElasticsearchRepository<Asset, String> {

    List<Asset> getAssetsByName(String name);

    List<Asset> getAssetsByIdIn(Collection<String> ids);

    List<Asset> getAssetsByCategoryId(String id);
}