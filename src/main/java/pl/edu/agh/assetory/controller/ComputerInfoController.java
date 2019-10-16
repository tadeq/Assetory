package pl.edu.agh.assetory.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import pl.edu.agh.assetory.model.client.ComputerInformation;
import pl.edu.agh.assetory.service.ComputerInformationService;

public class ComputerInfoController {
    private ComputerInformationService informationService;

    @PostMapping
    @ApiOperation(value = "saves new report from connected machine")
    public ResponseEntity<?> addComputerInformation(ComputerInformation info) {
        return ResponseEntity.ok(informationService.saveInformation(info));
    }
}
