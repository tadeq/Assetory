package pl.edu.agh.assetory.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.assetory.model.Asset;

import java.util.List;

@Repository
public interface AssetsRepository extends ElasticsearchRepository<Asset, String> {

    List<Asset> findAssetsByName(String name);

    Asset findAssetById(String id);
}