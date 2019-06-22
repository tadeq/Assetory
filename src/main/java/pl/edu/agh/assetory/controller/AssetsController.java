package pl.edu.agh.assetory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.edu.agh.assetory.model.Asset;
import pl.edu.agh.assetory.service.AssetsService;

@RestController
@RequestMapping(value = "/assets")
public class AssetsController {

    private AssetsService assetsService;

    @Autowired
    public AssetsController(AssetsService assetsService) {
        this.assetsService = assetsService;
    }

    @PostMapping(value = "/add")
    public String addAsset(@RequestBody Asset newAsset) {
        assetsService.addAsset(newAsset);
        return "Records saved in the db.";
    }

    @GetMapping(value = "/all")
    public Iterable<Asset> getAllAssets() {
        return assetsService.getAllAssets();
    }
}