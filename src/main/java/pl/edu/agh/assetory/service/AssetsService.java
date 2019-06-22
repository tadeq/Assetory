package pl.edu.agh.assetory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.agh.assetory.Repository.AssetsRepository;
import pl.edu.agh.assetory.model.Asset;

import java.util.List;


@Service
public class AssetsService {

    @Autowired
    @Qualifier("assetsRepository")
    private AssetsRepository assetsRepository;

    public List<Asset> findByName(String name) {
        return assetsRepository.findAssetsByName(name);
    }

    public Asset addAsset(Asset asset) {
        return assetsRepository.save(asset);
    }

    public Iterable<Asset> getAllAssets() {
        return assetsRepository.findAll();
    }

}